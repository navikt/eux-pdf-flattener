const pdfjsLib = require('pdfjs-dist');

pdfjsLib.getDocument('/medical.pdf').then(function (doc) {
    var numPages = doc.numPages;
    console.log('# Document Loaded');
    console.log('Number of Pages: ' + numPages);
    process.stdout.write('## Document Loaded')
    process.stdout.write('Number of Pages: ' + numPages)

})