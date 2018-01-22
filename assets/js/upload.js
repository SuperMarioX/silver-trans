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
//	var a =  new Array();
//    for(var e in array){
//        a.push(e.fileName);
//    }
//	$("#fileName").html(a.join(', '))
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
	$('.expand').width('0%');
	$("#loading").show();
	$("#browseButton").css("pointer-events","none");
});
r.on('complete', function() {
	console.info('complete');
	$("#loading").fadeOut("slow");
	$("#browseButton").css("pointer-events","auto");
});
r.on('progress', function() {
	var process = Math.round(r.progress() * 10000) / 100;
	$('.processbar').width(process+'%');
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
