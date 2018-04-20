## Description

Run image classification engine on [Apache PredictionIO](http://predictionio.incubator.apache.org) using a [Tensorflow](https://www.tensorflow.org) model. 

This template

* follows the workflow in official [Tensorflow tutorial for image classification](https://github.com/tensorflow/models/blob/master/tutorials/image/imagenet/classify_image.py).
* **uses a pre-trained model** from the inception challenge. The purpose of the engine is to be able to deploy a Tensorflow model and do inference via HTTP.
* **uses Tensorflow Java API**

## Version 1.0 Changes
* uses Tensorflow Java API

## Workflow
### Downloading the model
Run `data/download.sh` to download the pre-trained imagenet model.

### Testing the engine
There are two ways of serving data to the engine.

1. Put the target image in `data/images`. You can change this path in `engine.json`. Then use `image` param as the filename of the target image such as `curl -H "Content-Type: application/json" -d '{ "image":"cropped_panda.jpg" }' http://localhost:8000/queries.json`.

2. Use `data` param to send a UTF-8 encoded string of the target image data.

If all goes well, the engine will return a JSON result such as `{"predictions":[{"categories":"giant panda, panda, panda bear, coon bear, Ailuropoda melanoleuca","confidence":0.8910737037658691},{"categories":"indri, indris, Indri indri, Indri brevicaudatus","confidence":0.007790538016706705},{"categories":"lesser panda, red panda, panda, bear cat, cat bear, Ailurus fulgens","confidence":0.002959122648462653},{"categories":"custard apple","confidence":0.0014657712308689952},{"categories":"earthstar","confidence":0.0011742385104298592}]}`

