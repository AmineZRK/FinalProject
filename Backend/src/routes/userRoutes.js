const express = require('express');
const UserController = require('../controllers/userController');
const auth = require('../middleware/auth');
//inscription
router.post('/create-user', UserController.createUser);
//login
router.post('/login',UserController.login);