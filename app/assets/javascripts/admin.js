(function($){ // Img tab
	var imgForm = $(".imageToBeAdded").clone();
	$(".imageToBeAdded").remove();
	
	$("#addImg").click(function(){
		$("#imagesToBeAdded").prepend(imgForm.clone());
	});
	
	$("#addPiece").click(function(){
		var piece ={};
		piece['Name']=$('#pieceName').val();
		piece['Desc']=$('#pieceDesc').val();
		piece['Date']=$('#pieceDate').val();
		piece['Thumb']=$('#pieceThumb').val();
		piece['Kind']=$('#pieceKind').val();
		piece['images']=new Array();
		$('.imageToBeAdded').each(function(i, ob) {
		    var img={};
		    var obj = $(ob);
		    img['Name']=obj.find('.imgName').val();
		    img['Desc']=obj.find('.imgDesc').val();
		    img['Focus']=obj.find('.imgFocus').val();
		    img['Url']=obj.find('.imgUrl').val();
		    piece.images.push(img);
		});
		
		$.ajax({
		    type: "POST",
		    dataType: "json",
		    contentType: "application/json",
		    data: JSON.stringify(piece),
		    url: "/admin/addDbObject",
		    success: function(){
		    	console.log("Uploaded piece to database");
		    	$('#pieceToBeAdded').animate({
		    	    opacity: 0,
		    	    left: "-65%"
		    	}, 300, function(){
		    		$('#pieceToBeAdded').remove();
		    	});
		    }
		});
	});
})($);

(function($){ //Edit db tab
	function addImg(image){
		var img = '<div class="image"><table>';
		
		img += '<tr><td> </td><td> </td><td>Name: </td><td><u class="editable imgName">'+image.Name+'</u></td></tr>';
		img += '<tr><td> </td><td> </td><td>Description: </td><td><u class="editable imgDesc">'+image.Desc+'</u></td></tr>';
		img += '<tr><td> </td><td> </td><td>Focus: </td><td><u class="editable imgFocus">'+image.Focus+'</u></td></tr>';
		img += '<tr><td> </td><td> </td><td>Url: </td><td><u class="editable imgUrl">'+image.Url+'</u></td></tr>';

		img += '</table></div>'
		return img;
	}
	
	function updateDbField(){
		var field = $("#editDb");
		field.empty();
		$.getJSON("/admin/getDbJson",function(json){
			for(var i=0;i<json.length;i++){
				var p = json[i];
				var img = p.images;
				
				var pieceElement = '<div class="pieceEditDb"><table>';

				pieceElement += '<tr><td>Name: </td><td><u class="editable pieceName">'+p.Name+'</u></td></tr>';
				pieceElement += '<tr><td>Description: </td><td><u class="editable pieceDesc">'+p.Desc+'</u></td></tr>';
				pieceElement += '<tr><td>Date: </td><td><u class="editable pieceDate">'+p.Date+'</u></td></tr>';
				pieceElement += '<tr><td>Thumbnail: </td><td><u class="editable pieceKind">'+p.Kind+'</u></td></tr>';
				pieceElement += '<tr><td>Kind: </td><td><u class="editable pieceThumb">'+p.Thumb+'</u></td></tr>';
				pieceElement += '</table><div class="images">';

				for(var count=0;count<img.length;count++){
					pieceElement += addImg(img[count]);
				}
				pieceElement += '</div></div>';
				
				field.append(pieceElement);
			}
			$(".editable").editable(function(value, settings) {return(value);},{ 
			    indicator : '<img src="/assets/images/smallLoader.gif">',
			    tooltip   : "Click to edit...",
			    style  : "inherit"
			});
		});
	}
	
	$("#pushToDb").click(function(){
		var pieces = new Array();
		$(".pieceEditDb").each(function(i,p){
			var piece = {};
			
			piece['Name']=$(p).find(".pieceName").text();
			piece['Desc']=$(p).find(".pieceDesc").text();
			piece['Date']=$(p).find(".pieceDate").text();
			piece['Thumb']=$(p).find(".pieceThumb").text();
			piece['Kind']=$(p).find(".pieceKind").text();
			piece['images'] = new Array();
			
			
			$(p).find('.images').children('.image').each(function(_,i){
				var image = {};
				image['Name']=$(i).find(".imgName").text();
				image['Desc']=$(i).find(".imgDesc").text();
				image['Focus']=$(i).find(".imgFocus").text();
				image['Url']=$(i).find(".imgUrl").text();
				
				piece.images.push(image);
			});
			
			pieces.push(piece)
		});

    	console.log("Json: "+JSON.stringify(pieces));
    	
		$.ajax({
		    type: "POST",
		    dataType: "json",
		    contentType: "application/json",
		    data: JSON.stringify(pieces),
		    url: "/admin/replaceDb",
		    success: function(){
		    	console.log("Ajax complete");
		    	$('#editDb').animate({
		    	    opacity: 0,
		    	    left: "-65%"
		    	}, 300, function(){
		    		$('#editDb').remove();
		    	});
		    }
		});
	});
	
	$("#updateEditDb").click(updateDbField);
	updateDbField();
})($);


(function($){ //json tab
	var jsonEdit = $("#jsonEdit");
	
	$("#getExistingDb").click(function(){
		$.getJSON("/admin/getDbJson",function(json){
			jsonEdit.text(JSON.stringify(json, null, 4))
		});
	});
	$("#pushJsonToDb").click(function(){
		$.ajax({
		    type: "POST",
		    dataType: "json",
		    contentType: "application/json",
		    data: jsonEdit.val(),
		    url: "/admin/addJsonDb",
		    success: function(){
		    	console.log("Ajax complete");
		    }
		});
	});
	
	$("#pushRemJsonToDb").click(function(){
		$.ajax({
		    type: "POST",
		    dataType: "json",
		    contentType: "application/json",
		    data: jsonEdit.val(),
		    url: "/admin/replaceDb",
		    success: function(){
		    	console.log("Ajax complete");
		    }
		});
	});
	
})($);