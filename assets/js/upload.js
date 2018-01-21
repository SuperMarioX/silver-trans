var r = new Resumable({
	target : 'upload.action',
	method : 'octet',
	chunkSize : 1024 * 300,
	simultaneousUploads : 4,
	// testChunks: true,
	query : {
		upload_token : new Date().getTime()
	},
	throttleProgressCallbacks : 1
});

r.assignBrowse(document.getElementById('browseButton'));
r.assignDrop(document.getElementById('dropTarget'));

r.on('fileSuccess', function(file) {
	console.info('fileSuccess', file);
});
r.on('fileProgress', function(file) {
	// console.info('fileProgress', file);
});
r.on('fileAdded', function(file, event) {
	$("#fileName").html(file.fileName)
	r.upload();
	file.relativePath = $("#path").text().trim();
	console.info('fileAdded', file);
});
r.on('filesAdded', function(array) {
	r.upload();
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
});
r.on('complete', function() {
	console.info('complete');
});
r.on('progress', function() {
	var process = Math.round(r.progress() * 10000) / 100;
	$('#process').text(process + '%')
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
