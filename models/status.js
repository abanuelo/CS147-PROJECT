//DEPENDENCIES
var restful = require('node-restful');
var mongoose = restful.mongoose;

//SCHEMA
var statusSchema = new mongoose.Schema({
	username: String,
	email: String,
	password: String
    });

//RETURN MODELS
module.exports = restful.model('tblstatus', statusSchema);
