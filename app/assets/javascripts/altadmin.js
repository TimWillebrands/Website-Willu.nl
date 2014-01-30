var ItemTemplate;
var ImageTemplate;

$( document ).ready(function() {
	ItemTemplate = $(".item").clone();
	$(".item").remove();
	ImageTemplate = $(".image").clone();
	$(".image").remove();
	
	$(".btn-add-item").click(createItem);
	
	$.getJSON( "/altadmin/piece", function( data ) {
  		for (var i = 0; i < data.length; i++) {
  			addItem(data[i]);
  		}
  	});
  	
  	//$(".btn-add").click(function(blah){ createImage(blah.currentTarget); });
});

function saveItem(item){
	var url = item.itemId ? "/altadmin/piece/" + item.itemId : "/altadmin/piece/" + createNewPiece(item);
	
	itemJson = {};
	itemJson.Name = item.find(".val-name").text();
	itemJson.Desc = item.find(".val-desc").text();
	itemJson.Kind = item.find(".val-kind").text();
	itemJson.Date = item.find(".val-date").text();
	itemJson.Thumb = item.find(".val-thumb").text();
	itemJson.images = [];
	
	item.find(".image").each(function( index ) {
		imageJson = {};
		imageJson.Name = $(this).find(".img-name").text();
		imageJson.Desc = $(this).find(".img-desc").text();
		imageJson.Focus = $(this).find(".img-focus").text();
		imageJson.Url = $(this).find(".img-url").text();
		itemJson.images.push(imageJson);
	});
	
	$.ajax({
  		type: "PUT",
  		url: url,
  		data: {password: getPassword(), json : JSON.stringify(itemJson)}
	})
	.done(function() {
    	alert("Saved");
  	})
  	.fail(function() {
    	alert( "The password is fail and aids" );
  	});	
}

/*function editItem(item){	
	$.ajax({
  		type: "DELETE",
  		url: "/altadmin/checkpass",
  		data: {password: getPassword()}
	})
	.done(function() {
  	})
  	.fail(function() {
    	alert( "The password is fail and aids" );
  	});	
}*/

function editItem(item){	
	$.ajax({
  		type: "DELETE",
  		url: "/altadmin/checkpass",
  		data: {password: getPassword()}
	})
	.done(function() {
    	makeEditable(item.find(".val-name"));
    	makeEditable(item.find(".val-desc"));
    	makeEditable(item.find(".val-kind"));
    	makeEditable(item.find(".val-date"));
    	makeEditable(item.find(".val-thumb"));

    	makeEditable(item.find(".img-name"));
    	makeEditable(item.find(".img-desc"));
    	makeEditable(item.find(".img-focus"));
    	makeEditable(item.find(".img-url"));
    	
    	item.editable = true;
    	item.find(".btn-edit").prop("disabled",true);
  	})
  	.fail(function() {
    	alert( "The password is fail and aids" );
  	});	
}

function deleteItem(item){
	var url = item.itemId ? "/altadmin/piece/" + item.itemId : "/altadmin/checkpass";
	
	$.ajax({
  		type: "DELETE",
  		url: url,
  		data: {password: getPassword()}
	})
	.done(function() {
    	item.remove();
  	})
  	.fail(function() {
    	alert( "The password is fail and aids" );
  	});	
}

function createImage(item){
	var newImage = ImageTemplate.clone();
	newImage.css("display","block");
	item.find(".images").prepend(newImage);
	if(item.editable = true){
		makeEditable(item.find(".img-name"));
    	makeEditable(item.find(".img-desc"));
    	makeEditable(item.find(".img-focus"));
    	makeEditable(item.find(".img-url"));
	}
}

function createItem(){
	var newItem = ItemTemplate.clone();
	newItem.css("display","block");
	$("#items").prepend(newItem);
  	connectItemButtons(newItem);
}

function createNewPiece(item){
	var newPieceItemId;
	$.ajax({
  		type: "POST",
  		async: false,
  		url: "/altadmin/piece",
  		data: {password: getPassword()}
	})
	.done(function(data) {
		newPieceItemId = data;
	})
	.error(function(){
		alert("AIDS!");
	});
	item.itemId = newPieceItemId;
	return newPieceItemId;
}

function addItem(itemId){
	var newItem = ItemTemplate.clone();
	newItem.css("display","block");
			
	$.getJSON( "/altadmin/piece/" + itemId, function( data ) {
  		newItem.find(".val-name").text(data.Name);
  		newItem.find(".val-desc").text(data.Desc);
  		newItem.find(".val-kind").text(data.Kind);
  		newItem.find(".val-date").text(data.Date);
  		newItem.find(".val-thumb").text(data.Thumb);
  		newItem.find(".thumbnail-img").attr("src", data.Thumb);
  		for (var i = 0; i < data.images.length; i++) {
  			addImage(data.images[i], newItem.find(".images"));
  		}
  	});
  	
  	newItem.itemId = itemId;
	$("#items").prepend(newItem);
  	connectItemButtons(newItem);
}

function addImage(data, target){
	var newImage = ImageTemplate.clone();
	newImage.css("display","block");
	newImage.find(".img-name").text(data.Name);
	newImage.find(".img-desc").text(data.Desc);
	newImage.find(".img-focus").text(data.Focus);
	newImage.find(".img-url").text(data.Url);
	newImage.find(".img-thumb").attr("src", "/assets/images/thumbnails/thumbnail_"+ data.Name.replace(" ","_") +".png");
  	
	target.prepend(newImage);
}

function getPassword(){
	var pass = $("#pass-box").val();
	var hash = 0;
	if (pass.length == 0) return hash;
	for (i = 0; i < pass.length; i++) {
		cha = pass.charCodeAt(i);
		hash = ((hash<<5)-hash)+cha;
		hash = hash & hash; // Convert to 32bit integer
	}
	
	return hash;
}

function connectItemButtons(item){
	item.find(".btn-edit").click(function(){ editItem(item); });
	item.find(".btn-save").click(function(){ saveItem(item); });
	item.find(".btn-del").click(function(){ deleteItem(item); });
	item.find(".btn-add").click(function(){ createImage(item); });
}

function makeEditable(itemValue){
	itemValue.addClass("edit");
	
	itemValue.editable(function (value, settings) {
        return (value);
    }, {
        indicator: "saving...",
        type: 'textarea',
        event: 'click',
        /*submitdata: function (value, settings) {
            return {
                method: "post",
                myId: jQuery("#id").val(),
            };
        },*/
        select: true,
        onblur: function () {
            $(".edit").find("form").submit();
        }
    });
}
