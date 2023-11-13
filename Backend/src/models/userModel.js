const mongoose = require('mongoose');
//const crypto = require('crypto')
const DataSchema = new mongoose.Schema({
    name: {
        type: String,
        required: [true, 'Please enter name'],
        trim: true,
        maxLength: [100, 'Product name cannot exceed 100 characters']
    },
    email: {
        type: String,
        required: true,
        unique: [true, 'entre un unique mail']
    },
    password:{
        type: String,
        required: true
    },
    phone: {
        type: String,
        required: true
    },
    role: {
        type: Boolean,
        required: true,
        default: false
    },
    
    
},{ timestamps: true, versionKey:false })


const User = mongoose.model('User', DataSchema, 'users');
module.exports = User;