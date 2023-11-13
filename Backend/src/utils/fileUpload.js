// utils/fileUpload

const path = require("path");
const multer = require("multer");

const storage = (destination)=> multer.diskStorage({
    destination: destination,
    filename: (req, file, cb) => {
        return cb(null, `${file.fieldname}_${Date.now()}${path.extname(file.originalname)}`)
    }
})


const fileUpload = (destination) =>multer({

    
    storage: storage(destination),
    limits: {
        fileSize:  10 * 1024 * 1024, //2mb,
        
    },
    fileFilter: (req, file, cb) => {
      const allowedMimeTypes = ['image/png', 'image/jpg', 'image/jpeg'];
    
      if (allowedMimeTypes.includes(file.mimetype)) {
        cb(null, true);
      } else {
        cb(new Error('Only .png, .jpg, and .jpeg formats are allowed!'), false);
      }
    },
    
      onError : function(err, next) {
        return console.log('error', err);
        next(err);
      }
}).single('photo')



module.exports = fileUpload;