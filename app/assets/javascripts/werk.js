//Variables
var imgCont = $("#imageFrame");
var menBar = $("#menuBar");
var navBar = $("#navbar");
var mainImg = $("#image");
var imgDesc = $("#imgDesc");

var imgDescBounce = true;
var pieces = [];
var curentPiece;

//Functions
isInt = function(n) {
	return typeof n === 'number' && n % 1 == 0;
}

resize = function(ow,oh){
    var w = $(window).width() - ow;
    var h = $(window).height() - oh;
    imgCont.css("width",(w).toString() + 'px');
    imgCont.css("height",(h).toString() + 'px');
    menBar.css("height",(h).toString() + 'px');
}

getBgImg = function(bgId){      	      	
	return $('<img>').attr('src', function () {
        var imgUrl = bgId.css('background-image');
        imgUrl = imgUrl.substring(4, imgUrl.length - 1);
    	return imgUrl;
    });
}

replaceImage = function(imageObj) {
	mainImg.css('opacity', '0');
    mainImg.css('background-image', 'url(' + imageObj.url + ')');
    mainImg.css('background-position', imageObj.focus);

    imagesLoaded( getBgImg(mainImg),function () {
    	mainImg.css('opacity', '1');
    });
}

descDown = function(animated){
	if(animated){
		$("#imgDesc").animate({bottom: 0}, 700);
	}else{
		$("#imgDesc").css("bottom",(-($("#imgDesc").height() - $("#descBtn").height() -5)) + "px");
	}	
}

changePiece = function(pieceId,imgId) { // int, int
	if(typeof curentPiece === 'undefined' || !(pieceId in pieces)){
		$.ajax({
		    type: "GET",
		    dataType: "json",
		    contentType: "application/json",
		    async: false,
		    url: "/item/"+ pieceId,
		    success: function(piece){	
		    	piece['id'] = pieceId;
		    	piece['imgId'] = 0;
		    	
				curentPiece = piece;
				pieces[pieceId] = curentPiece;
		    }
		});
	}else if(curentPiece.id != pieceId){
		curentPiece = pieces[pieceId];
	}
	
	if(isInt(parseInt(imgId))){ //if imgId is an integer
		replaceImage(curentPiece.images[imgId]);
	}else{
		replaceImage(curentPiece.images[curentPiece.imgId]);
	}
	
    $('#imgDesc h1').html( curentPiece.name );
    $('#imgDesc p').html( curentPiece.desc );
    $('#imgMenu ul').empty();
    
    for(var i = 0; i < curentPiece.images.length; i++){
    	$('#imgMenu ul').append('<li><img class="imgMenuImage" id="imgId_' +i+'" src="'+ curentPiece.images[i].url +'" height="60" ></li>');
    }
    $("#imgMenu").mCustomScrollbar("update");
}

//Binding events and other script logic

$(".menuButton").click(function(){
    changePiece($(this).attr('id').replace('piece_', ''));
});

$(document).on('click', '.imgMenuImage', function(){
    changePiece(curentPiece.id,$(this).attr('id').replace('imgId_', ''));
});

var bounce = false;
$("#descBtn").click(function(){
	if(bounce){
    	bounce = false;
    	var h = (-($("#imgDesc").height() - $("#descBtn").height() -5)) + "px";
    	$("#imgDesc").animate({bottom: h}, 700);
	}else{
		descDown(true);
		bounce = true;
	}
});


$(window).resize(function() {
    resize(menBar.outerWidth() , navBar.height());
});

$(".active").removeClass("active");
$("#werk").parent().addClass("active");

resize(menBar.outerWidth() , navBar.height());
changePiece($(".menuButton").first().attr('id').replace('piece_', ''));
descDown(false);
$("#imgMenu").mCustomScrollbar({horizontalScroll:true,theme:"dark"});
$(".vertical").mCustomScrollbar({theme:"dark"});
