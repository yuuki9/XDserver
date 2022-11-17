/*********************************************************
 * 엔진 파일을 로드합니다.
 * 파일은 asm.js파일, html.mem파일, js 파일 순으로 로드하며,
 * 로드 시 버전 명(engineVersion)을 적용합니다.
 *********************************************************/
var ENGINE_PATH = "./static/js/engine/"
var engineVersion = "v0.0.0.1";
var LSMD_LAYER = null; //"lsmd_svc_view_seoul"; // 기본 서울
; (function() {

	var tm = (new Date()).getTime();	// 캐싱 방지

	// 1. XDWorldEM.asm.js 파일 로드
	var file = ENGINE_PATH + "XDWorldEM.asm.js?tm=" + engineVersion;
	var xhr = new XMLHttpRequest();
	xhr.open('GET', file, true);
	xhr.onload = function() {

		var script = document.createElement('script');
		script.innerHTML = xhr.responseText;
		document.body.appendChild(script);

		// 2. XDWorldEM.html.mem 파일 로드
		setTimeout(function() {
			(function() {

				var memoryInitializer = ENGINE_PATH + "XDWorldEM.html.mem?tm=" + engineVersion;
				var xhr = Module['memoryInitializerRequest'] = new XMLHttpRequest();
				xhr.open('GET', memoryInitializer, true);
				xhr.responseType = 'arraybuffer';

				xhr.onload = function() {

					// 3. XDWorldEM.js 파일 로드
					var url = ENGINE_PATH + "XDWorldEM.js?tm=" + engineVersion;
					var xhr = new XMLHttpRequest();
					xhr.open('GET', url, true);
					xhr.onload = function() {
						var script = document.createElement('script');
						script.innerHTML = xhr.responseText;
						document.body.appendChild(script);
					};
					xhr.send(null);
				}
				xhr.send(null);
			})();
		}, 1);
	};
	xhr.send(null);

})();


/*********************************************************
 *	엔진파일 로드 후 Module 객체가 생성되며,
 *  Module 객체를 통해 API 클래스에 접근 할 수 있습니다. 
 *	 - Module.postRun : 엔진파일 로드 후 실행할 함수를 연결합니다.
 *	 - Module.canvas : 지도를 표시할 canvas 엘리먼트를 연결합니다.
 *********************************************************/

/**
* 	화면 크기 구하기 W
**/
function returnWidths() {
	return $(window).width();
}

/**
* 	화면 크기 구하기 H
**/
function returnHeights() {
	return $(window).height();
}

var Module = {
	TOTAL_MEMORY: 256 * 1024 * 1024,
	postRun: [init],
	canvas: (function() {
		var canvas = document.getElementById('canvas');
		// Canvas 스타일 설정
		canvas.style.position = "fixed";
		canvas.style.top = "0px";
		canvas.style.left = "0px";

		canvas.addEventListener("contextmenu", function(e) {
			e.preventDefault();
		});

		// 화면 저장을 위해 버퍼 설정이 필요합니다.
		var context = canvas.getContext("experimental-webgl", {
			preserveDrawingBuffer: true

		});

		return canvas;
	})()
};

//브이월드 정보
var Vworld = {
	Api_key: '767B7ADF-10BA-3D86-AB7E-02816B5B92E9',
	Url1: 'http://xdworld.vworld.kr:8080',
	Url: 'https://xdworld.vworld.kr',
	Port: 8080,
	Wms_Url: 'http://api.vworld.kr',
	Wms_key: 'A632FC0F-1DE6-3FB1-AD2B-64B09AA41FB6',
	Wms_workspace: '/req/wms?',
	Wfs_workspace: '/req/wfs?',
	Wms_domain: 'localhost',
	Wms_port: 80
};

//var mapServer = "https://egisxd.duplanet.kr";
var mapServer = "http://localhost:8088";

/* 엔진 로드 후 실행할 초기화 함수(Module.postRun) */
function init() {

	Module.SetAPIKey(Vworld.Api_key);
	Module.XDESetSatUrlLayerName(Vworld.Url, "tile"); // tile 설정
	Module.XDESetDemUrlLayerName(Vworld.Url, "dem"); // dem 설정

	// 엔진 초기화 API 호출(필수)
	Module.Start(returnWidths(), returnHeights());

	// 이벤트 설정
	initEvent(Module.canvas)

	// 오브젝트 선택 이벤트 (6) 
	Module.XDSetMouseState(Module.MML_SELECT_POINT);

	// 건물 레이어 추가
	//Module.XDEMapCreateLayer("_MT01",mapServer,0,true,true,false,9,0,12); // 패스트파이브 모델링 건물 레이어
	//Module.XDEMapCreateLayer("facility_build_user", mapServer, 0, true, true, false, 9, 0, 12);
	//Module.XDEMapCreateLayer("facility_build_user_mina", mapServer, 0, true, true, false, 9, 0, 12);
	//Module.XDEMapCreateLayer("test_1", mapServer, 0, true, true, false, 9, 0, 12);
	Module.XDEMapCreateLayer("facility_build_user", mapServer, 0, true, true, false, 9, 0, 15);
	Module.XDEMapCreateLayer("facility_build_vw", mapServer, 0, true, true, false, 9, 0, 15);
	
	// 카메라 설정
	//Module.getViewCamera().setLocation(new Module.JSVector3D(126.9784147, 37.5666805, 500.0));
	
	Module.getViewCamera().moveLonLatAlt(127.123828660123, 37.5149494697931, 150, true);
	Module.getViewCamera().setTilt(50);
}


function initEvent(_canvas) {
	/* 브라우저 이벤트 등록 */
	window.onresize = function(e) {
		Module.Resize(returnWidths(), returnHeights());
		Module.XDRenderData();
	};


	//레이어 클릭 
	_canvas.addEventListener("Fire_EventSelectedObject", function(e) {
		console.log(e)
		var vPosition = Module.getMap().ScreenToMapPointEX(new Module.JSVector2D(e.idx, e.idy));
		console.log(vPosition)

	});
	
	Module.onclick


}
/*********************************************************/
var GLOBAL = {
	layer: null,	// 고스트 심볼 오브젝트 저장 레이어
	object: [],	// 고스트 심볼 오브젝트
	blob: []
};


/* 고스트 심볼 XDO import */
function importXDO1(_fileNm, _h, _id) {

	Module.getViewCamera().setLocation(new Module.JSVector3D(126.9784147, 37.5666805, 1500.0));

	// 고스트 심볼 저장 레이어 생성
	var layerList = new Module.JSLayerList(true);
	GLOBAL.layer = layerList.createLayer("GHOSTSYMBOL_XDO", Module.ELT_GHOST_3DSYMBOL);

	// 원점 (0, 0, 0), 평면 기반의 xdo 포맷 데이터 로드
	Module.getGhostSymbolMap().insert({

		id: _id,
		//url : ENGINE_PATH+"input.xdo",
		url: ENGINE_PATH + _fileNm,
		format: "xdo",         // url 정보에 확장자가 없는 경우 format을 xdo로 명시해 주시면 xdo 파일로 인식됩니다
		version: "3.0.0.2",	// XDO 버전 설정(3.0.0.2 혹은 3.0.0.1)

		callback: function(e) {

			Module.getGhostSymbolMap().setModelTexture({
				//건물(GhostSymbol)명 
				id: _id,
				//텍스처 등록되야 할 면적 인덱스
				face_index: 0,
				//텍스처 파일 경로
				url: "http://dev-egis.duplanet.kr/api/buildlib/MT04_07_4/xdo/jpg?lod=3",
				//텍스처 파일 등록 완료시 콜백
				callback: function(e) {
					console.log("확인1")
				}
			});

			// 모델 크기 반환
			var objectSize = Module.getGhostSymbolMap().getGhostSymbolSize(e.id);

			// 고스트심볼 오브젝트 생성
			GLOBAL.object[0] = createGhostSymbol(
				"a0000002",
				e.id,
				objectSize,
				[126.9784147, 37.5666805, parseFloat(_h)] //원본
			);

			GLOBAL.layer.addObject(GLOBAL.object[0], 0);
		}
	});
}

function importXDO2(_fileNm, _h, _id) {

	Module.getViewCamera().setLocation(new Module.JSVector3D(126.9784147, 37.5666805, 1500.0));

	// 고스트 심볼 저장 레이어 생성
	//var layerList = new Module.JSLayerList(true);
	//GLOBAL.layer = layerList.createLayer("GHOSTSYMBOL_XDO", Module.ELT_GHOST_3DSYMBOL);

	// 원점 (0, 0, 0), 평면 기반의 xdo 포맷 데이터 로드
	Module.getGhostSymbolMap().insert({

		id: _id,
		//url : ENGINE_PATH+"input.xdo",
		url: ENGINE_PATH + _fileNm,
		format: "xdo",         // url 정보에 확장자가 없는 경우 format을 xdo로 명시해 주시면 xdo 파일로 인식됩니다
		version: "3.0.0.2",	// XDO 버전 설정(3.0.0.2 혹은 3.0.0.1)

		callback: function(e) {

			Module.getGhostSymbolMap().setModelTexture({
				//건물(GhostSymbol)명 
				id: _id,
				//텍스처 등록되야 할 면적 인덱스
				face_index: 0,
				//텍스처 파일 경로
				url: "http://dev-egis.duplanet.kr/api/buildlib/MT01_07_1/xdo/jpg?lod=3",
				//텍스처 파일 등록 완료시 콜백
				callback: function(e) {
					console.log("확인2")
				}
			});

			// 모델 크기 반환
			var objectSize = Module.getGhostSymbolMap().getGhostSymbolSize(e.id);

			// 고스트심볼 오브젝트 생성
			GLOBAL.object[1] = createGhostSymbol(
				"d0000003",
				e.id,
				objectSize,
				[126.9784147, 37.5666805, parseFloat(_h)] //원본
			);

			GLOBAL.layer.addObject(GLOBAL.object[1], 0);
		}
	});
}

/* 고스트 심볼 XDO export */

function exportXDO() {
   let today = new Date().getTime(); 
	console.log(today)
	if (GLOBAL.object.length == 0) {
		return;
	}
	
	// real3d 타일 레이어 포맷의 XDO 파일 export
	for(var i=0; i<GLOBAL.object.length;i++){
		var bytes = GLOBAL.object[i].exportFileFormat({
			format: "xdo",
			version: "3.0.0.2",
			worldPosition: true,
			texture: {
				level: 3,
				fileName: "test_"+i+".jpg"
			}
		});

		if (bytes == null) {
			return;
		}
		
	// 반환 된 바이트를 파일로 저장
	var len = bytes.length;
	var buf = new ArrayBuffer(len);
	var view = new Uint8Array(buf);

	var j;
	for (j = 0; j < len; j++) {
		view[j] = bytes[j];
	}

	GLOBAL.blob[i] = new Blob([new Uint8Array(view).buffer]);
	console.log(GLOBAL.blob[i])
	var formData = new FormData();
	formData.append("blob", GLOBAL.blob[i]);
	formData.append("fileNm",  "test_"+i+".xdo");
	formData.append("modelId", "MT01_07_1_"+len);
	formData.append("lon", GLOBAL.object[i].position.Longitude);
	formData.append("lat", GLOBAL.object[i].position.Latitude);
	formData.append("alt", GLOBAL.object[i].position.Altitude);
	formData.append("direct", 45);
	formData.append("height", 10.45);
	
	$.ajax({
		method: "POST",
		url: 'http://localhost:18080/api/prj/test',
		// url: 'http://dev-egis.duplanet.kr/api/prj/test',
		processData: false,
		contentType: false,
		data: formData,
		beforeSend: function() {
		}, success: function(data) {
			console.log(data+"_"+i);
		}

	})
		
	}

	/*$.ajax({
		method: "GET",
		//url: 'http://localhost:18080/api/prj/read?buildId=' + 24,
		url : 'http://localhost:8088/XDServer/build/saveData',
		processData: false,
		contentType: false,
		beforeSend: function() {
		}, success: function(data) {
			console.log(data);
		}

	})*/

}

/* 고스트 심볼 모델 오브젝트 생성 */
function createGhostSymbol(_objectKey, _modelKey, _size, _position) {

	var newModel = Module.createGhostSymbol(_objectKey);

	newModel.setRotation(0.0, 0.0, 0.0);
	newModel.setScale(new Module.JSSize3D(1.0, 1.0, 1.0));
	newModel.setGhostSymbol(_modelKey);
	newModel.setBasePoint(0.0, -_size.height * 0.5, 0.0);
	newModel.setPosition(new Module.JSVector3D(_position[0], _position[1], _position[2]));

	return newModel;
}

function Utf8ArrayToStr(array) {
	var out, i, len, c;
	var char2, char3;

	out = "";
	len = array.length;
	i = 0;
	while (i < len) {
		c = array[i++];
		switch (c >> 4) {
			case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
				// 0xxxxxxx
				out += String.fromCharCode(c);
				break;
			case 12: case 13:
				// 110x xxxx   10xx xxxx
				char2 = array[i++];
				out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
				break;
			case 14:
				// 1110 xxxx  10xx xxxx  10xx xxxx
				char2 = array[i++];
				char3 = array[i++];
				out += String.fromCharCode(((c & 0x0F) << 12) |
					((char2 & 0x3F) << 6) |
					((char3 & 0x3F) << 0));
				break;
		}
	}

	return out;
}

// ------------------------------------------------- 브이월드 타일 계산 코드
// VWorld 타일의 레벨 당 길이 (단위 degree)
function getDistanceFromLevel(_level) {
	if (isNaN(_level)) return null;
	if (_level == 0) return 36;
	else return 36 / (Math.pow(2, _level))
}

// 숫자를 문자열 변환 중 앞자리 0 채움
function leadingZeros(n) {
	var zero = '';
	n = n.toString();

	digits = 8;
	if (n.length < 5) digits = 4;

	if (n.length < digits) {
		for (var i = 0; i < digits - n.length; i++)
			zero += '0';
	}
	return zero + n;
}

// 입력 인자
// var options = { level : n, longitude : flon, latitude : flat }
function getTileInfoFromPosition(_options) {
	var rst = {
		level: _options.level,     // 레벨 출력
		x: 0,
		y: 0,
		idx: 0,                    // 타일 idx (number)
		idy: 0,                    // 타일 idy (number)
		idxstr: "0000",            // 타일 idx 문자열 경로 지정 (자릿수 0 채움)
		idystr: "0000",            // 타일 idy 문자열 경로 지정 (자릿수 0 채움)
		path: "",                  // 서버의 타일 경로 계산
	}

	if (_options.longitude > 360 || _options.longitude < 0 || 180 < _options.latitude || _options.latitude < 0) return null;

	var levelDist = getDistanceFromLevel(_options.level);

	rst.x = _options.longitude;
	rst.y = _options.latitude;

	rst.idx = Math.floor((180 + _options.longitude) / levelDist);
	rst.idy = Math.floor((90 + _options.latitude) / levelDist);

	rst.idxstr = leadingZeros(rst.idx);
	rst.idystr = leadingZeros(rst.idy);

	rst.path = '/' + _options.level + '/' + rst.idystr + '/' + rst.idystr + '_' + rst.idxstr;

	return rst;
}

//================================================================
function proxy() {
	$.ajax({
		method: "GET",
		// CONTEXTPATH+"/proxy?url=" + encodeURIComponent(geoserverURL);
		url: 'http://localhost:18080/XDServer/proxy?url='+encodeURIComponent("http://xdworld.vworld.kr:8080"),
		processData: false,
		contentType: false,
		success: function(data) {
			url = data.url;
		}
	})
	
}

function cameraMove(){
	Module.getViewCamera().setLocation(new Module.JSVector3D(128.64259157603368, 35.8885570147165, 1500.0));
}
