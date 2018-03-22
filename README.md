# parse_androiddex

fork from https://github.com/fourbrother/parse_androiddex

fix :

```
if(byteAry.length == 1){
	size = byteAry[0];
}else if(byteAry.length == 2){
	size = Utils.byte2int(byteAry);
}else if(byteAry.length == 4){
	size = Utils.byte2int(byteAry);
}
```	
to 

```
if(byteAry.length == 1){
	size = byteAry[0];
}else if(byteAry.length == 2){
	size = Utils.decodeUleb128(byteAry);
}else if(byteAry.length == 4){
	size = Utils.decodeUleb128(byteAry);
}
```

fix decodeUleb128() method


add: 

* if some class's method code_off is 0, support to ingore it.
* add calchecksum method and calsignature method
* add getMethodSignStr method
* add code about clean method's insns
* shield the info's output

