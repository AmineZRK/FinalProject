// routes/wasteRoutes.js
const express = require('express');
const router = express.Router();
const fileUpload = require('../utils/fileUpload')
const wasteController = require('../controllers/wasteController');

// Define API routes
router.post('/waste',fileUpload("./storage/images"), wasteController.createWaste);
// Add other routes as needed

router.post('/upload',fileUpload("./uploads"), wasteController.createWaste)
module.exports = router;
