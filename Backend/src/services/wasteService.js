// services/wasteService.js
const Waste = require('../models/wasteModel');

exports.getAllWastes = async () => {
  try {
    const wastes = await Waste.find();
    return wastes;
  } catch (error) {
    throw new Error('Error fetching wastes from the database');
  }
};

// Add other service functions (e.g., getWasteById, updateWaste, deleteWaste) as needed
