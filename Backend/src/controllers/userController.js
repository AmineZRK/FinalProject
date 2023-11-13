const User = require("../models/User");
const mongoose = require("mongoose");
const bcrypt = require('bcrypt');
const pwRules = require('../utils/password');
const { Validator } = require('node-input-validator');
const fs = require('fs');
const DIR = './';
const jwt = require('jsonwebtoken');




module.exports = class UserController {

  //New User Create
  static createUser = async (req, res) => {
    let payload = req.body; 
    console.log(payload.name);

    
    
    const emailExists = async email => !!(await User.findOne({email}).catch(e => false));
    const Exist =await emailExists(payload.email)
if(!Exist){
      if (pwRules.validate(payload.password)){
        bcrypt.hash(payload.password,10)
        .then(hash => {
          const newId = new mongoose.Types.ObjectId();
          const userCreate = new User({
            _id: newId,
            name: payload.name,
            email: payload.email,
            password: hash,
            phone: payload.phone,
            role: false
          });
          console.log(userCreate);
          userCreate.save()
          .then(() => res.status(200).json({ 
            code: 200,
            message: "User Create Successfully",
            data: userCreate, }))
        })
        .catch ((error) =>
              res.status(501).json({
                code: 501,
                message: error.message,
                error: true,
              }));
        
        
      } else {
        return res.status(500).json({
                code: 500,
                message: "password Invalid",
              });
    }
  }else{
    return res.status(501).json({
      code: 500,
      message: "mail alredy used",
    });
  }
      
  };


  //Single User Information
  static singleUser = async(req, res)=>{
    const id = req.params.id;

    try{
      const singleUserInfo = await User.findById(id);
      const {name, email, phone, role} =singleUserInfo;

      //return console.log(getImageName);
      
      const singleuUserData ={
        name,
        email,
        phone,
        role
      }
      return res.status(200).json({
        code: 200,
        message: "User Information",
        data: singleuUserData,
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

  //login
  static login = async(req,res)=>{
    const reqemail = req.body.email;
    const reqpassword=req.body.password;
    try{
      const singleUserInfo = await User.findOne({email: reqemail});
      if(singleUserInfo){
        const {_id, password, role} =singleUserInfo;
        const singleuUserData ={
          _id,
          password,
          role
        }
        bcrypt.compare(reqpassword,singleuUserData.password).then(
          (data)=>{
            if(data){
              const token=jwt.sign(
                { 
                    userId: _id, 
                    role: role 
                },
                'RANDOM_TOKEN_SECRET',
                { expiresIn: '24h' }
            )
              //res.cookie('jwt',token, {  expiresIn: '24h' , httpOnly: true })
              res.status(200).json({ 
                userId: _id, 
                token: jwt.sign(
                    { 
                        userId: _id, 
                        role: role 
                    },
                    'RANDOM_TOKEN_SECRET',
                    { expiresIn: '24h' }
                ),
                isAdmin: role
            });
            }else{
              return res.status(401).json({
                code: 500,
                message: "password incorrect",
              });
            }
          }
        )
        
      }else{
        return res.status(501).json({
          code: 500,
          message: "email incorrect",
        });
      }
      
    
    }catch(error){
      res.status(501).json({
        code: 501,
        message: error.message,
        error: true,
      });
    }
  }





  //All User information
  static allUser = async(req, res)=>{
    const isAdmin=res.locals.isAdmin;

    try{
      if(isAdmin){
        const allUserInfo = await User.find().select('-password');
      return res.status(200).json({
        code: 200,
        message: "User Information",
        data: allUserInfo,
      });
      }else{
        res.status(400).json({
          code: 400,
          message: 'you dont have acces',
          error: true,
        });
      }
    }
    catch(error){
      res.status(501).json({
        code: 501,
        message: error.message,
        error: true,
      });
    }
  }


  //User Update by User Id
  static updateUser = async(req, res)=>{
    
    if (res.locals.userId){
      const id = res.locals.userId
      let reqBody = req.body
      
          
      try{
        if (req.body.password){
          console.log("enter")
            bcrypt.hash(req.body.password,10)
            .then(hash=>{
              reqBody.password=hash
              User.findOneAndUpdate( { _id: id }, reqBody,{ projection: { _id: 0, password: 0 } } ).then(updateItem=>{
                return res.status(200).json({
                  code: 200,
                  message: "User Update Information Successfully",
                  data: updateItem
                });
              });
              
            })}else{
              
              User.findOneAndUpdate( { _id: id }, req.body,{ projection: { _id: 0, password: 0 } } ).then(updateItem=>{
                return res.status(200).json({
                  code: 200,
                  message: "User Update Information Successfully",
                  data: updateItem
                });
              });
            }
          
            
      
        
          


      }catch(error){
        res.status(501).json({
          code: 501,
          message: error.message,
          error: true,
        });
      }

    }else{
      return res.status(501).json({
        code: 500,
        message: "auth required",
      });
    }
    
  }


  //User Delete By User Id
  static deleteUser = async(req, res)=>{
    
    try{
      const id = res.locals.userId;
      const isAdmin=res.locals.isAdmin;
      const iduser = req.params.id
      console.log(res.locals.token)
      if(isAdmin ||(id==iduser)){
        const userDeleteinfo = await User.findOneAndDelete({_id: iduser});

        //const userDelete = await User.deleteOne({_id: id});
        return res.status(200).json({
          code: 200,
          message: "User Delete Successfully",
          data: userDeleteinfo,
        });
      }else{
        return res.status(401).json({
          code: 401,
          message: "you dont have acces",
          data: userDeleteinfo,
        });
      }
        
    }catch(error){
      res.status(501).json({
        code: 501,
        message: error.message,
        error: true,
      });
    }
  }

  //Update add admin

  static AddAdmin = async(req, res)=>{

    const idUser = req.params.id

    const isAdmin = res.locals.isAdmin

    var reqBody = req.body 
    if (isAdmin){

      const id = res.locals.userId

      let reqBody = req.body

      try{

          console.log(reqBody)

          const updateItem = await User.findOneAndUpdate( { _id: idUser }, reqBody,{ projection: { _id: 0, password: 0 } } );



     

       

          return res.status(200).json({

            code: 200,

            message: "User Update Information Successfully",

            data: updateItem

          });




      }catch(error){

        res.status(501).json({

          code: 501,

          message: error.message,

          error: true,

        });

      }



    }else{

      return res.status(501).json({

        code: 500,

        message: "auth required",

      });

    }

   

  }


};
