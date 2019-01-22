using NpgsqlTypes;
using System;
using System.IO;
using System.Threading.Tasks;
using Amazon.S3;
using Amazon.S3.Model;
using pinpointr.Models;
using Microsoft.AspNetCore.Http;

namespace pinpointr.Helpers {
    public class AmazonS3Service
    {
       private static String accessKey = "YOUR_ACCESS_KEY_ID";
       private static String accessSecret = "YOUR_SECRET_ACCESS_KEY";
       private static String bucket = "YOUR_S3_BUCKET";

       public static async Task<UploadPhotoModel> UploadObject(IFormFile file)
       {
           // connecting to the client
           var client = new AmazonS3Client(accessKey, accessSecret, Amazon.RegionEndpoint.EUCentral1);

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
                   BucketName = bucket,
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