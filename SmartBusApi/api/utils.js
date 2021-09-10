const util = require('util');

async function send(res, statusCode, error, response) {
    res.statusCode = statusCode
    // If null then skip
    let err = error
    if(error) {
      err = await jsonifyError(error)
    }
    res.send({
        status: statusCode,
        error: err,
        response: response
    })
}

jsonifyError = (error) => new Promise( (resolve, reject) => {
  JSON.stringify(error, (name, value) => {
      if (value instanceof Error) {
          resolve(util.format(value));
      } else {
          resolve(value);
      }
  })
})

function toJson(value) {
  return util.format(value)
}

function getDate() {
    var date = new Date();
    return formatDate(date)
}

function formatDate(date) {
    return
    ("0" + (date.getDate())).slice(-2) + '/' +
    ("0" + (date.getMonth() + 1)).slice(-2) + '/' +
    date.getFullYear()
}

function FileName(){
    let date_ob = new Date();
    let date = ("0" + date_ob.getDate()).slice(-2);

    // current month
    let month = ("0" + (date_ob.getMonth() + 1)).slice(-2);

    // current year
    let year = date_ob.getFullYear();

    // current hours
    let hours = date_ob.getHours();

    // current minutes
    let minutes = date_ob.getMinutes();

    // current seconds
    let seconds = date_ob.getSeconds(); 
    return (year + month  + date + "-" + hours + minutes + seconds)
}

function DateTime(){
    let date_ob = new Date();
    let date = ("0" + date_ob.getDate()).slice(-2);

    // current month
    let month = ("0" + (date_ob.getMonth() + 1)).slice(-2);

    // current year
    let year = date_ob.getFullYear();

    // current hours
    let hours = date_ob.getHours();

    // current minutes
    let minutes = date_ob.getMinutes();

    // current seconds
    let seconds = date_ob.getSeconds(); 
    return (year + "-" + month + "-" + date + " " + hours + ":" + minutes + ":" + seconds)
}

module.exports = {
  toJson,
  send,
  getDate,
  formatDate,
  DateTime,
  FileName
}
