const pdfjsLib = require('pdfjs-dist');

var args = process.argv.slice(2);

pdfjsLib.getDocument(args[0]).promise.then(function (doc) {
    var numPages = doc.numPages;
    console.log('# Document Loaded');
    console.log('Number of Pages: ' + numPages);
    process.stdout.write('## Document Loaded')
    process.stdout.write('Number of Pages: ' + numPages)

})
