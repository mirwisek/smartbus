let {
	create,
	updateBus,
	deleteUser,
	getCurrentLoc,
	doesUserExist,
	login,
	checkUserExist,
	updatepass,
	getAllUsers,
	deleteBus
} = require("./service");
require("promise");
const jwt = require('jsonwebtoken');
const nodemailer = require('nodemailer');

const JWT_SECRET = process.env.JWT_SECRET

// For admin management
getAccounts = () => new Promise((resolve, reject) => {
	getAllUsers().then(results => {
		resolve(results)
	}).catch(e => {
		reject(e)
	})
});

createUser = (body) => new Promise((resolve, reject) => {
	// console.log("body: "+JSON.stringify(body))
	doesUserExist(body).then(emailResult => {
		if (emailResult.length > 0) {
			resolve({
				userType: emailResult.usertype,
				userName: emailResult.username,
				isNewUser: false
			})
		} else {
			create(body).then((results) => {
				isNewUser: true
				resolve(results)
			}).catch((error) => {
				reject(error)
			})
		}
	}).catch(e => {
		reject(e)
	})
});

getBuses = () => new Promise((resolve, reject) => {
	// Convert Tiny Bit (1 | 0) to Boolean (true | false)
	getBusDetail().then(results => {
		results.forEach(element => {
			if(element['isonline'] === 1)
				element['isonline'] = true
			else if(element['isonline'] === 0)
				element['isonline'] = false
		});
		resolve(results)
	}).catch(e => {
		reject(e)
	})
});

updateB = (body) => new Promise((resolve, reject) => {
	updateBus(body).then((results) => {
		resolve(results)
	}).catch((error) => {
		reject(error)
	});
});

deleteU = (body) => new Promise((resolve, reject) => {
	// Delete from both tables
	deleteUser(body).then((r) => {
		if(r.affectedRows > 0) {
			deleteBus(body).then((r2) => {
				// Registeration might contain the entity but the bus might not in case of student
				if(r2.affectedRows == 0)
					resolve(r)
				else
					resolve({'affectedRows': -100})
			}).catch((error) => { reject(error) });
		} else {
			resolve({'affectedRows': -100})
		}
	}).catch((e) => { reject(e) });
});

loginU = (body) => new Promise((resolve, reject) => {
	login(body).then((results) => {
		if (results) {
			resolve(results)
		} else {
			resolve(error)
		}
	}).catch((e) => {
		reject(e)
	})
});

// Step 1
let transporter = nodemailer.createTransport({
	service: 'gmail',
	auth: {
		user: process.env.EMAIL, // TODO: your gmail account
		pass: process.env.PASSWORD // TODO: your gmail password
	}
});

// Step 2
let mailOptions = {
	from: 'johntesta987@gmail.com', // TODO: email sender
	to: "",
	subject: 'Nodemailer - Test',
	text: 'Wooohooo it works!!'
};

// Step 3
sendEmail = (url, recepient) => new Promise((resolve, reject) => {
	mailOptions['to'] = recepient
	mailOptions['text'] = 'You can reset the password at ' + url;
	// console.log("mail: "+JSON.stringify(mailOptions))
	transporter.sendMail(mailOptions, (err, data) => {
		if (err) {
			reject(err);
		} else {
			// send(null, 200, null, "Email sent...")
			resolve(data);
		}
	})
});

postemail = (body) => new Promise((resolve, reject) => {
	// console.log("beforeC: " + JSON.stringify(body))
	checkUserExist(body).then((results) => {
		// console.log("afterC: " + JSON.stringify(results.length))
		const email = body.email
		if (results.length > 0) {

			secret = JWT_SECRET + results[0].password,
				payload = {
					email: results.email
				},
				token = jwt.sign(payload, secret, { expiresIn: '15m' }),
				link = `http://localhost:3006/reset-password/${email}/${token}`
			// console.log('email: '+email)
			sendEmail(link, email).then(emailData => {
				resolve(emailData)
			}).catch(e => {
				reject(e)
			})
		} else { resolve(results) }
	}).catch((error) => {
		reject(error)
	});
})

getparams = (body) => new Promise((resolve, reject) => {
	const { token } = body;
	// console.log("body: "+JSON.stringify(body))
	// console.log("TokenC: "+JSON.stringify(token))
	// console.log("EmailC: "+JSON.stringify(email))

	checkUserExist(body).then((results) => {
		// console.log("controler: "+results[0].password)
		// console.log("controler: "+results.length)
		if (results.length > 0) {
			const secret = JWT_SECRET + results[0].password;
			const payload = jwt.verify(token, secret);
			// console.log("afterC: "+JSON.stringify(payload))
			resolve(results)

		} else { resolve(results) }
	}).catch((error) => {
		// console.log(error)
		reject(error)
	});
})

postparams = (body, params) => new Promise((resolve, reject) => {
	const { email, token } = params;
	const { password } = body;
	// console.log("tokenC: " + JSON.stringify(token))
	// console.log("emailC: " + JSON.stringify(email))
	// console.log("passC: " + JSON.stringify(password))

	checkUserExist(params).then((results) => {
		// console.log("controler: "+results)
		if (results.length > 0) {
			// if(password != nuLL && password != ""){} todo when running check password and email
			const secret = JWT_SECRET + results[0].password;
			const payload = jwt.verify(token, secret);
			// we can simply find the user with the payload email and id  and finally update with new password
			if (password != null && password != "") {
				update = {
					email: email,
					password: password
				}
				updatepass(update).then(r => {
					if (r.length > 0) {
						resolve(r)
					} else {
						resolve(r)
					}
				}).catch(e => {
					reject(e)
				})
			} else { resolve("Password is Empty/Null") }

		} else { resolve(results) }

	}).catch((error) => {
		// console.log(error)
		reject(error)
	});
})


module.exports = {
	getAccounts,
	createUser,
	deleteU,
	updateB,
	getBuses,
	loginU,
	postemail,
	getparams,
	postparams
};