// app.js
const express = require('express');
const mongoose = require('mongoose');
const dotenv = require('dotenv');
const multer = require("multer");
const cors = require('cors');
dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

const URI = "mongodb://127.0.0.1:27017/wasteApp";

// Connect to MongoDB using promise syntax
mongoose.connect(URI)
  .then(() => {
    console.log('Connected to MongoDB');
  })
  .catch((err) => {
    console.error('MongoDB connection error:', err);
  }); 
// Middleware
app.use(express.json());
app.use(cors());
app.use('/uploads', express.static('uploads')); // Serve uploaded files statically
app.use('/waste', express.static('uploads'))
// Routes
const wasteRoutes = require('./src/routes/wasteRoutes');
app.use('/api', wasteRoutes);
 

app.use((err, req, res, next) => {
    if (err instanceof multer.MulterError) { // Multer-specific errors
        return res.status(418).json({
            err_code: err.code,
            err_message: err.message,
        });
    } else { // Handling errors for any other cases from whole application
        return res.status(500).json({
            err_code: 409,
            err_message: "Something went wrong!"
        });
    }
});

// Start the server
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
