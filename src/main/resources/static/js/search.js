function mysearch(){
	var q = $("#q").val();
	if(q.length<1){
		layer.msg('Search for...');
		return null;
	}
	window.location.href='/search?query='+q;
}