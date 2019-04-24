using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Blob;
using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;

namespace pinpointrAPI.Helpers
{
    public class uploadHelper
    {

        public bool uploadBlob(IFormFile in_file)
        {
            CloudStorageAccount storageAccount = null;
            CloudBlobContainer blobContainer = null;
            CloudBlobClient blobClient = null;
            CloudBlockBlob blockBlob = null;
            bool uploadStatus = false;
            //bool checkStatus = false;

            Console.WriteLine("Recieved request to upload blob");
            string connectionString = getAZBConnectionString();
            Console.WriteLine("Attempting to connect to Azure blob storage with: {0}", connectionString);
            if (CloudStorageAccount.TryParse(connectionString, out storageAccount))
            {
                Console.WriteLine("Connection string is valid");
                blobClient = storageAccount.CreateCloudBlobClient();
                blobContainer = Task.Run(async () => { return await makeContainer(blobClient); }).Result;
                Console.WriteLine("Uploading {0} to {1}", in_file.Name, blobContainer.Name);
                blockBlob = blobContainer.GetBlockBlobReference(in_file.Name);
                uploadStatus = Task.Run(async () => { return await uploadStream(blockBlob, in_file.OpenReadStream()); }).Result;
                if (uploadStatus)
                {
                    Task.Run(async () => { return await listBlobs(blobContainer); });
                }
            } else
            {
                Console.WriteLine("Invalid connection string");
                return false;
            }
            return uploadStatus;
        }

        public async Task<bool> uploadStream(CloudBlockBlob in_blockBlob, Stream in_fs)
        {
            await in_blockBlob.UploadFromStreamAsync(in_fs);
            return true;
        }

        private string getAZBConnectionString()
        {
            List<string> lines = new List<string>();
            string line;
            using (StreamReader sr = new StreamReader("cred.data"))
            {
                while ((line = sr.ReadLine()) != null)
                {
                    lines.Add(line);
                    Console.WriteLine(line);
                }
            }
            return lines[0];
        }

        private async Task<bool> listBlobs(CloudBlobContainer in_blobContainer)
        {
            List<IListBlobItem> listBlobs = new List<IListBlobItem>();
            Console.WriteLine("List blobs in container:");
            BlobContinuationToken continuationToken = null;
            do
            {
                var results = await in_blobContainer.ListBlobsSegmentedAsync(null, continuationToken);
                //Get the value of the continuation token returned by the listing call
                continuationToken = results.ContinuationToken;
                foreach (IListBlobItem item in results.Results)
                {
                    Console.WriteLine(item.Uri);
                    listBlobs.Add(item);
                }
            } while (continuationToken != null); //lloop while continuation token is not null
            return true;
        }

        private async Task<bool> uploadFile(CloudBlockBlob in_blockBlob, string fileName)
        {
            await in_blockBlob.UploadFromFileAsync(fileName);
            return true;
        }

        private async Task<CloudBlobContainer> makeContainer(CloudBlobClient in_blobClient)
        {
            Console.WriteLine("attempting to create a new container");
            CloudBlobContainer blobContainer = in_blobClient.GetContainerReference("upload" + Guid.NewGuid().ToString());
            await blobContainer.CreateAsync();
            //Set blob permissions
            BlobContainerPermissions permissions = new BlobContainerPermissions
            {
                PublicAccess = BlobContainerPublicAccessType.Blob
            };
            await blobContainer.SetPermissionsAsync(permissions);
            return blobContainer;
        }

    }
}
