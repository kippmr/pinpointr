using NpgsqlTypes;
using System;
using System.IO;
using System.Threading.Tasks;
using Amazon.S3;
using Amazon.S3.Model;
using pinpointr.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using System.Diagnostics;

namespace pinpointr.Helpers {
    public class AmazonS3Service
    {

       public static async Task<UploadPhotoModel> UploadObject(IFormFile file)
        {

            var config = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("appsettings.json").Build();


            BucketConnection _bucket = new BucketConnection
            {
                access_key = config["BucketConnection:access_key"],
                access_secret = config["BucketConnection:access_secret"],
                bucket = config["BucketConnection:bucket"]
            };
                Debug.WriteLine(_bucket.access_secret);

           // connecting to the client
           var client = new AmazonS3Client(_bucket.access_key, _bucket.access_secret, Amazon.RegionEndpoint.USEast2);

           // get the file and convert it to the byte[]
           byte[] fileBytes = new Byte[file.Length];
           file.OpenReadStream().Read(fileBytes, 0, Int32.Parse(file.Length.ToString()));
          
           // create unique file name for prevent the mess
           var fileName = Guid.NewGuid() + file.FileName;

           PutObjectResponse response = null;

           using (var stream = new MemoryStream(fileBytes))
           {
               var request = new PutObjectRequest
               {
                   BucketName = _bucket.bucket,
                   Key = fileName,
                   InputStream = stream,
                   ContentType = file.ContentType,
                   CannedACL = S3CannedACL.PublicRead
               };

               response = await client.PutObjectAsync(request);
           };

           if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
           {
               // this model is up to you, in my case I have to use it following;
               return new UploadPhotoModel
               {
                   success = true,
                   file_name = fileName
               };
           }
           else
           {
               // this model is up to you, in my case I have to use it following;
               return new UploadPhotoModel
               {
                   success = false,
                   file_name = fileName
               };
           }
       }
    }
}