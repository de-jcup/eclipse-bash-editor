# Testscript for #91 - function not correct handled
do_whatis_ports() { 
	
	#xxxx
	lm -'%.s-' {1..$x} # <-- this the reason! "$x}" makes the problem ->"%x }" would make no problems!"
}