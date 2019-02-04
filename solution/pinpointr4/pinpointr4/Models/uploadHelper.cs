using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Blob;
using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace pinpointrAPI.Models
{
    public class uploadHelper
    {
        public void getConnectionInfo()
        {
            Console.WriteLine(System.IO.Directory.GetCurrentDirectory());
            CloudStorageAccount storageAccount = null;
            CloudBlobContainer blobContainer = null;
            string storageConnectionInfo = getAZBConnectionString();
            Console.WriteLine(storageConnectionInfo);
            if (CloudStorageAccount.TryParse(storageConnectionInfo, out storageAccount))
            {
                Console.WriteLine("Recieved valid connection info");
                CloudBlobClient cloudBlobClient = storageAccount.CreateCloudBlobClient();
                blobContainer = Task.Run(async () => { return await makeContainer(cloudBlobClient); }).Result;
                string localPath = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
                string localFileName = "quicktest" + Guid.NewGuid().ToString() + ".txt";

                using (StreamWriter sw = new StreamWriter(localPath + localFileName))
                {
                    sw.WriteLine("This is a test file");
                }
                Console.WriteLine("Uploading to blob storage as '{0}'", localFileName);
                CloudBlockBlob blockBlob = blobContainer.GetBlockBlobReference(localFileName);
                bool status = Task.Run(async () => { return await uploadFile(blockBlob, localPath + localFileName); }).Result;
                status = Task.Run(async () => { return await listBlobs(blobContainer); }).Result;
                   

            } else
            {
                Console.WriteLine("Invalid connection string");
            }
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
            CloudBlobContainer blobContainer = in_blobClient.GetContainerReference("testblob" + Guid.NewGuid().ToString());
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
