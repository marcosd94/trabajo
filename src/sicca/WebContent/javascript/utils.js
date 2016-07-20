var link= false;
function clickMenu(idItem){	
	 
	if(!link && idItem!=null && idItem.length != 0){
	
		document.getElementById(idItem).click();
		link = false;
	}else{
		link = true;
	}    
}
function confirmar() {
	if(!confirm("¿Realmente desea realizar la operacion?")) {
		return false; // no se realiza
	} else {
		return true; // si se realiza
       }
}

// Verifica si se ingreso solo nombre o apellido
function verficarNombreApellido(nombre, apellido) {
	if(nombre!= null && apellido != null){
		if(nombre.value!='' && apellido.value == ''){
			alert("Debe escribir un apellido");
			return false;
		}else if( apellido.value!='' && nombre.value==''){
			alert("Debe escribir un nombre");
			return false;
		}
		else {			
			return true;
		}		
	}else{		
		return true;
	}
}

function addToBodyOnload(oFunction) {
    var existingOnload = window.onload;
    window.onload = function () { oFunction(); existingOnload(); };
}

function validarEmail(valor) {
	var cadena = valor.value;
	if(valor.value != ""){
	    re=/^[_a-z0-9-]+(.[_a-z0-9-]+)*@[a-z0-9-]+(.[a-z0-9-]+)*(.[a-z]{2,3})$/;
	    if(!re.exec(cadena)){
	        alert("Ingrese un e-mail valido.");
	        valor.focus();
	    	return false;
	    }else{
	        return true;
	    }
	}else{
		return true;
	}
}

function ponerBarra(e){
	var obj=getObj(e);
	var divisor="/";
	if(esNumerico(e) && enablePut(obj)){
		if (obj.value.length==2 || obj.value.length==5) {
			obj.value+=divisor;
		}
	}
}
function esNumerico(e){
	return (e.keyCode>47 && 58>e.keyCode) || (e.keyCode>95 && 106>e.keyCode);
}

function soloNumerosConDecimales(evt){
	// asignamos el valor de la tecla a keynum
	if(window.event){// IE
		keynum = evt.keyCode;
	}else{
		keynum = evt.which;
	}
	
	// comprobamos si se encuentra en el rango
	if((keynum>47 && keynum<58) || keynum == 8 || keynum == 46){
		return true;
	}else{
		return false;
	}
}
function separadorMilesEspecial( sValue , separador){
    sValue=  sValue.replace( /\./g, "" );
    var sRegExp = new RegExp('(-?[0-9a-zA-Z]+)([0-9a-zA-Z]{3})');
    while(sRegExp.test(sValue)) {    	
        sValue = sValue.replace(sRegExp, '$1'+separador+'$2');      
    }
    return sValue;
}

function enablePut(cadena){
	return cadena.toString().split("/").length<=2;
	
}

function getTresDecimales(numero){
	return (Math.round(numero*1000))/1000;
}

function getObj(e) {
	if(document.all){
   		return document.activeElement;
   	}
   	else{
   		return e.currentTarget;
   	}
}

function hideSpan(elSpan){
    elSpan.style.display = 'none';  
}
function showSpan(elSpan){
    elSpan.style.display = 'inline';    
}

function separadorMiles( sValue , separador){
    sValue=  sValue.replace( /\./g, "" );
    var sRegExp = new RegExp('(-?[0-9]+)([0-9]{3})');
    while(sRegExp.test(sValue)) {
        sValue = sValue.replace(sRegExp, '$1'+separador+'$2');
    }
    return sValue;
}

function separadorMilesEspecial( sValue , separador){
    sValue=  sValue.replace( /\./g, "" );
    var sRegExp = new RegExp('(-?[0-9a-zA-Z]+)([0-9a-zA-Z]{3})');
    while(sRegExp.test(sValue)) {    	
        sValue = sValue.replace(sRegExp, '$1'+separador+'$2');      
    }
    return sValue;
}

// Admite solamente numeros, Backspace, Enter y teclas de direccion
// (izq,der,arr,ab)
function soloNumeros(evt) {
	var code;
	if (!evt) var e = window.event;
	if (evt.keyCode) code = evt.keyCode;
	else if (evt.which) code = evt.which;
	var character = String.fromCharCode(code);
	/*
	 * JUANBER -> Quitado
	 *    code == 37 ||   code == 38 || code == 46||
	 *  */
	if(code == 8 || code == 13 ||code == 37 ||   code == 38 || code == 46||  code == 39 || code == 40||  code == 9){
		return true;
	}
	
	if (isNaN(character) || code == 32 || code < 48 || code > 57) {
		return false;
	} else {
		return true;
	}
}


function ponerDosPuntos(e){
    
    var obj=getObj(e);
    var simb=":";
    if(esNumerico(e)){
          if (obj.value.length==2) {
                obj.value+=simb;
          }
    }
}

	function ponerPunto(e){
	    
	    var obj=getObj(e);
	    var divisor=".";
	    if(esNumerico(e) && enablePutPunto(obj)){
	          if (obj.value.length==2) {
	                obj.value+=divisor;
	          }
	    }
	}
	function setValue(elId,elValor){	
		
		document.getElementById(elId).value = elValor;
		return false;
	}

	function hideModal(campoError,elModal){
		if(document.getElementById(campoError).value == 'Error'){			 
			document.getElementById(elModal).style.display = "none";			 
		} 
		else{
			 ;
		}
		return false;	
	}
	function submitForm(elId){
		 document.forms[elId].submit();
	}

	function dataTableUtil(laFilaVar,rowIndex){
		console.log(laFilaVar);	
		console.log(rowIndex);
		console.log(rowIndex[0]);
		console.log(laFilaVar.initialData[rowIndex[0]]);
	}

	// Verifica si se ha superado la longitud maxima. Utilizado normalmente en
	// en los textarea's.
	// Ej: onkeypress="return imposeMaxLength(event, this, 1000);"
	function imposeMaxLength(evt, Object, MaxLen)
	{
		var code;
		if (!evt) var e = window.event;
		if (evt.keyCode) code = evt.keyCode;
		else if (evt.which) code = evt.which;
		var character = String.fromCharCode(code);		
		if(code == 8 || code == 37 || code == 38 || code == 39 || code == 40){
			return true;
		}
		
	    return (Object.value.length <= MaxLen);
	}
	
	
	
	function agregarValidaciones() {
		for (var i=0; input = document.getElementsByTagName('input')[i]; i++){
				if (input.className.trim()=='rich-calendar-input'){
					if (!input.onblur) {
						input.onblur=validarFormato;
					}
					if (!input.onkeyup) {
						input.onkeyup=ponerBarra;
					}
				}
				else if (input.className.trim()=='entero') {
					input.onblur=validarFormato;
					// input.onkeypress=numero_entero_form;
				}	
		}
	}
	
	
	function validarFormato(e){
		if (!e) {
			return;
		}
		
		var obj= getObj(e);
		var span=document.getElementById("spanMen"+obj.id);
		if (span!=null) {
			obj.parentNode.removeChild(span);
		}
		if (obj.value.length==0) {
			return;
		}
		var mensaje;
		var sRegExp;
		var insertarAlado=false;
		var noValido = false;
		
		switch (obj.className.trim()) {
		case "rich-calendar-input":
			mensaje="Formato de fecha no v\u00E1lido. Formato: dd/mm/aaaa";
			sRegExp=new RegExp('((0)[1-9]|[1-2][0-9]|(3)[0-1])/((0)[1-9]|(1)[0-2])/\\d{4}');
			if (!(obj.value.length==10)){
				noValido = true;
			}
			
			break;
		case "entero":
			mensaje="Formato num\u00E9rico no v\u00E1lido";
			sRegExp=new RegExp('^[0-9]*$');
			insertarAlado=true;
			break;
		
		case "entero-negativo":
			mensaje="Formato num\u00E9rico no v\u00E1lido";
			sRegExp=new RegExp('^([0-9]*|-[0-9]*)$');
			insertarAlado=true;
			break;
		default:
			break;
		}
		
		if ( !(sRegExp.test(obj.value)) || noValido){
			var spanTag = document.createElement("span");
			spanTag.id = "spanMen"+obj.id;
	        spanTag.className ="required";
	        spanTag.innerHTML = mensaje;
			getSpaceToAppend(obj, insertarAlado);
			objToAppend.parentNode.insertBefore(spanTag,objToAppend);
			
		}
	}
	
	var objToAppend;
	function getSpaceToAppend(o, insALado) {
		objToAppend=null;
		if (insALado) {
			objToAppend=o.nextSibling;
			return;
		}
		objToAppend=o.parentNode.lastChild;
		
	}
	
	function format(input)
	{
		var num = input.value.replace(/\./g,"");
		if(!isNaN(num)){
			num = num.toString().split("").reverse().join("").replace(/(?=\d*\.?)(\d{3})/g,'$1.');
			num = num.split("").reverse().join("").replace(/^[\.]/,"");
			input.value = num;
		 }
		else{ 
			input.value = input.value.replace(/[^\d\.]*/g,"");
		}
	}
