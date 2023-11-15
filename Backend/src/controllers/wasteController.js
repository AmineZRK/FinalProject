// controllers/wasteController.js
const multer = require('multer');
const path = require('path');
const Waste = require('../models/wasteModel');

module.exports = class wasteController {
  static createWaste = async (req, res) => {
    console.log('111111111111111111',req.file);
    try {
      let payload = req.body;
      console.log('file added', req.body);
      console.log('data ====>', payload);
      // Image check if available, then include the image into payload
      let imgUrl = "";
      if (req.file) {
        imgUrl = `storage/images/${req.file.filename}`;
        payload.image = imgUrl;
      } 

      // Destructure fields from req.body
      const { latitude, longitude, user, wasteType, weightEstimation } = req.body;

      // Extracting image name from the URL
      const getImageName = payload.image.match(/\/([^\/?#]+)[^\/]*$/);

      // Create a new waste object
      const newWaste = await new Waste({
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude),
        user,
        wasteType,
        weightEstimation,
        photoUrl: `http://localhost:3000/waste/${getImageName[1]}`,
        photos: [{ url: payload.image }], // Assuming each waste has one photo for simplicity
      }).save();
      return res.status(200).json({
        code: 200,
        message: "created",
        data: newWaste,
      });
    } catch (error) {
      console.error(error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  };

  static upload = async (req, res) => {

    try{
      console.log(req.file)
      if (req.file) {
        // File saved successfully
        res.status(200).send('File uploaded and saved successfully.');
      } else {
        // Error saving the file
        res.status(500).send('Error saving the file.');
      }
    
      // return res.status(200).json({
      //     code: 200,
      //     message: "created"
      //   }); 
    }catch(error){
        console.error(error);
        res.status(505).json({ error: 'Internal Server Error' });
        console.log(error)
    }
 
  }

  static alldata = async (req, res) => {
    try{
      const allData = await Waste.find();
      

      //return console.log(singleUserInfo);
      return res.status(200).json({
        code: 200,
        message: "User Information",
        data: allData,
      });
    }
    catch(error){
      res.status(501).json({
        code: 501,
        message: error.message,
        error: true,
      });
    }
  }

};
