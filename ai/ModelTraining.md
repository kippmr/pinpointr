# Training the Model & Updating the Android app 

## 1.	Put your Dataset Directory in the tf_files Directory
The dataset should contain a root and list of sub-directories, with the root named as the set of classes/objects, and each subdirectory named as a class/object and filled with images of that class/object. Note: there must be at least 2 class directories.
## 2.	Navigate to Root of Project 
```bash
cd [Scene-Sense Project Folder] 
```

Depending on permissions, a virtual environment may be needed: 
 
```bash 
virtualenv venv

virtualenv venv --system-site-packages

source venv/bin/activate
```

## 3.	Set Image Size Parameters
```bash
IMAGE_SIZE=224
ARCHITECTURE="mobilenet_0.50_${IMAGE_SIZE}"
```

## 4.	Start Tensorboard
```bash
tensorboard --logdir tf_files/training_summaries &
```
(ignore warnings) 

Note: if already running:
```bash
pkill -f "tensorboard"
```

## 5.	Run the Training
```bash
python -m scripts.retrain \
  --bottleneck_dir=tf_files/bottlenecks \
  --model_dir=tf_files/models/"${ARCHITECTURE}" \
  --summaries_dir=tf_files/training_summaries/"${ARCHITECTURE}" \
  --output_graph=tf_files/retrained_graph.pb \
  --output_labels=tf_files/retrained_labels.txt \
  --architecture="${ARCHITECTURE}" \
  --image_dir=tf_files/[Class Set Directory]
  ```

## 6.	Convert the Retrained Graph
```bash
IMAGE_SIZE=224
toco \
  --graph_def_file=tf_files/retrained_graph.pb \
  --input_file=tf_files/retrained_graph.pb \
  --output_file=tf_files/optimized_graph.lite \
  --input_format=TENSORFLOW_GRAPHDEF \
  --output_format=TFLITE \
  --input_shape=1,${IMAGE_SIZE},${IMAGE_SIZE},3 \
  --input_array=input \
  --output_array=final_result \
  --inference_type=FLOAT \
  --input_data_type=FLOAT
  ```

## 7.	Copy the Optimized Graph.lite file and the labels .txt file from the tf_files folder into the Android App’s assets  
```bash
cp tf_files/optimized_graph.lite android/SceneSense/app/src/main/assets/graph.lite 
cp tf_files/retrained_labels.txt android/SceneSense/app/src/main/assets/labels.txt 
```
## 8.	Resync the Gradle (Android Studio)
## 9.	Run the App 

### Video to Image Conversion

Take a video (or several) of the object, moving around the object, getting as many different angles and different lighting combinations as possible. Make sure you are keeping the object in focus at all times of the video.


### 1.	Go to Video Directory
```bash
ffmpeg -i training_photos.mp4 thumb%04d.jpg -hide_banner
```

### 2.	Move photos to folder in tf_files with folder name as category name (NOTE: must be at least 2 category folders). Also remember to remove the video (.mp4) files 

