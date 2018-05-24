var r = new Resumable({
	target : 'upload.action',
	//method : 'octet',
	chunkSize : 1024 * 500,
	forceChunkSize:1024*500,
	simultaneousUploads : 4,
	maxFiles:1,
	maxFileSize:1024*1024*1024*100,
	fileType:[],
	headers : {'token' : getCookie('token')},
	testChunks: false
});

r.assignBrowse(document.getElementById('browseButton'));
r.assignDrop(document.getElementById('dropTarget'));

r.on('fileSuccess', function(file) {
	console.info('fileSuccess', file);
});
r.on('fileProgress', function(file) {
	console.info('fileProgress', file);
	file.relativePath = $('#path').text().trim();
});
r.on('fileAdded', function(file, event) {
	$('#fileName').html(file.fileName)
	r.upload();
	//file.relativePath = $('#path').text().trim();
	console.info('fileAdded', file);
});
r.on('filesAdded', function(array) {
	r.upload();
//	var a =  new Array();
//    for(var e in array){
//        a.push(e.fileName);
//    }
//	$('#fileName').html(a.join(', '))
	console.info('filesAdded', array);
});
r.on('fileRetry', function(file) {
	console.info('fileRetry', file);
});
r.on('fileError', function(file, message) {
	console.info('fileError', file, message);
});
r.on('uploadStart', function() {
	console.info('uploadStart');
	$('.processbar').show();
	$('#browseButton').css('pointer-events','none');
});
r.on('complete', function() {
	console.info('complete');
	$('#browseButton').css('pointer-events','auto');
	$('.processbar').fadeOut();
});
var process_0;
r.on('progress', function() {
	var process = Math.round(r.progress() * 1000) / 10;
	if(process_0 != process){
	    process_0 = process;
	    $('.processbar').width(process+'%');
	    $('#process').text(process + '%');
	}
});
r.on('error', function(message, file) {
	console.info('error', message, file);
});
r.on('pause', function() {
	console.info('pause');
});
r.on('cancel', function() {
	console.info('cancel');
});
