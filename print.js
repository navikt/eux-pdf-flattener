const htmlPdf = require('html-pdf-chrome');

const options = {
    port: 9222,
    completionTrigger: new htmlPdf.CompletionTrigger.Timer(50000),
    chromeFlags: ['--disable-web-security', '--headless']
};

var args = process.argv.slice(2);

const url = 'http://localhost:8888/web/viewer.html?file=/in/' + args[0];


let pdf = htmlPdf.create(url, options).then((pdf) => pdf.toFile('/tmp/pdf.js/out/' + args[0]));