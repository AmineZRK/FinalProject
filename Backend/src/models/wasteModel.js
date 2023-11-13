const mongoose = require('mongoose');

const photoSchema = new mongoose.Schema({
  url: {
    type: String,
    required: true,
  },
});

const wasteSchema = new mongoose.Schema({
  latitude: {
    type: Number,
    required: true,
  },
  longitude: {
    type: Number,
    required: true,
  },
  user: {
    type: String,
    required: true,
  },
  wasteType: {
    type: String,
    required: true,
  },
  weightEstimation: {
    type: String,
    required: true,
  },
  photoUrl: {
    type: String,
    required: true,
  },
  photos: [photoSchema], // Change this line to accept an array of photos
}, {
  timestamps: true, // Adds createdAt and updatedAt timestamps
});

const Waste = mongoose.model('Waste', wasteSchema);

module.exports = Waste;
