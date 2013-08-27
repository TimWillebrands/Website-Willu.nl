function createButton(id){
	return '<button id="drivePieceBtnId'+ id +'" type="button" class="drivePieceBtn">Put in database</button>';
}

function connectClick(className){
	var nk = '.key';
	var nv = '.value';
	var ik = '.imgKey';
	var iv = '.imgValue';
	
	$('.'+className).click(function(btn){
		var tableName = '#drivePieceForm' + $(this).attr('id').replace('drivePieceBtnId','');
		var object = {};
		
		var img;
		var curentImg = 0;
		var images = new Array();
		
		$(tableName +' tbody').children().each(function(i) { 
			var n = $(this).find('.key').length > 0;
			var i = $(this).find('.imgKeyNumber').length > 0;
			var k = n ? nk : ik;
			var v = n ? nv : iv;
			var info = findKeyValue($(this),k,v);
			
			if(n){
				object[info.key]=info.value;
			}else{
				if(i){
					curentImg = $(this).find('.imgKeyNumber').html();
					img = newImgObj(images,curentImg);
				}
				img[info.key]=info.value;
			}
		});
		
		object['images']=images;

		$.ajax({
		    type: "POST",
		    dataType: "text",
		    contentType: "application/json",
		    data: JSON.stringify(object),
		    url: "/admin/addDbObject",
		    success: function(){
		    	$(tableName).animate({
		    	    opacity: 0,
		    	    left: "-65%"
		    	  }, 300, function(){
			    	$(tableName).remove();
		    	});
		    }
		});
	});
}

function newImgObj(arr,key){
	arr[key]={};
	return arr[key];
}

function findKeyValue(parentElement,key,value){
	var info = new Array();
	info['key']=parentElement.find(key).html();
	info['value']=parentElement.find(value).find('u').html();
	return info;
} 

function processRow(level,item,emptyFirst){
	var grandTotal = "";
	var ef = emptyFirst;
	
	for(var key in item){
		var val = item[key];
		var newNode = ef ? "" : "<tr>";
		var newNodeEnd = ef ? "" : "</tr>";
		var emptyCell = ef ? "" : "<td></td>";
		ef = false;
		
		for(var i=0;i<level;i++){
			newNode += emptyCell;
		}
		
        if(val instanceof Array){ //if image array
        	newNode += "<td>Images: </td><td"+ key +"</td>"+processRow(level+1,val,true);
        }else if(val instanceof Object){
        	newNode += "<td class='imgKeyNumber'>"+ key +"</td>"+processRow(level+1,val,true);
        }else if(!emptyFirst){ //if object is normal value
        	newNode += "<td class='key'>"+ key +"</td><td class='value'><u class='editable' style='display: inline,cursor:pointer' title='Click to edit...'>"+ val +"</u></td>";
        }else if(emptyFirst){ //if value belongs to an image
        	newNode += "<td class='imgKey'>"+ key +"</td><td class='imgValue'><u class='editable' style='display: inline,cursor:pointer' title='Click to edit...'>"+ val +"</u></td>";
        }
		grandTotal += newNode + newNodeEnd;
    }
	return grandTotal;
}

(function($,form){
	
	
	var active = true;
	$("#folderBtn").click(function(){	
		if (!active) return;
		
		active=false;
		$("#folderBtn").html('Please wait <img src="/assets/images/smallLoader.gif" height="12" width="12">');
		form.innerHTML = "";	
				    
	    $.ajax({
	    	type:"POST",
		    url:'/admin/getDrivePieces/' + $("#foldername").val(),
			dataType: "json",
		    timeout:25000,
		    async: true
		}).done(function ( folder ) {
			var newNode = "";
			
			for(var i=0;i<folder.length;i++){
		        newNode += createButton(i) + '<table id="drivePieceForm' + i + 
		        	'"class="drive-json">'+ processRow(0,folder[i])+ '</table>';
		    }
		    
			form.innerHTML = newNode;
			
			$(".editable").editable(function(value, settings) {return(value);}, 
			{ 
			    indicator : '<img src="/assets/images/smallLoader.gif">',
			    tooltip   : "Click to edit...",
			    style  : "inherit"
			});
			
			connectClick('drivePieceBtn');
			$("#folderBtn").html('Get Folder');
			$("#table-container").mCustomScrollbar({theme:"dark"});
			document.body.style["overflow-y"]="hidden";
			active = true;
		}).fail(function ( ) {
			console.log("fail");
		});
	});
})($,document.getElementById('drive-form'));