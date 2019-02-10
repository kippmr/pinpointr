using System;
using System.IO;
using System.Threading.Tasks;
using Amazon.S3;
using Amazon.S3.Model;
using pinpointrAPI.Models;
using Microsoft.AspNetCore.Http;
using System.Diagnostics;

namespace pinpointrAPI.Helpers
{
    public class S3Helper
    {
/// <summary>
        /// Uploads file to S3 bucket connection
        /// </summary>
        /// <param name="fileName">unique filename</param>
        /// <param name="file">file to be uploaded</param>
        /// <param name="_bucket">bucket connection</param>
        /// <returns></returns>
        public static async Task<UploadPhotoModel> UploadObject(string fileName, IFormFile file, BucketConnection _bucket)
        {

            // connecting to the client
            var client = new AmazonS3Client(_bucket.access_key, _bucket.access_secret, Amazon.RegionEndpoint.USEast2);

            // get the file and convert it to the byte[]
            byte[] fileBytes = new Byte[file.Length];
            file.OpenReadStream().Read(fileBytes, 0, Int32.Parse(file.Length.ToString()));

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
                // success
                return new UploadPhotoModel
                {
                    success = true,
                    file_name = fileName
                };
            }
            else
            {
                // fail
                return new UploadPhotoModel
                {
                    success = false,
                    file_name = fileName
                };
            }
        }
    }
}