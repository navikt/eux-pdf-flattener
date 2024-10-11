const pdfjsLib = require('pdfjs-dist');

var args = process.argv.slice(2);

pdfjsLib.getDocument({ url: args[0], enableXfa : true }).promise.then(function (doc) {
    var numPages = doc.numPages;
    //console.log('# Document Loaded');
    //console.log('Number of Pages: ' + numPages);
    console.log(numPages);
    //process.stdout.write('## Document Loaded')
    //process.stdout.write('Number of Pages: ' + numPages)
    process.stdout.write(numPages)

})
