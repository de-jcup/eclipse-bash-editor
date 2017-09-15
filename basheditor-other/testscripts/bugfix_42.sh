alpha() {
	# Just a test case for bracket support:

 	a=${b}
    c={d[e]}
    f=${g[h]}
    # next line made problems with bracket switching support
    i={j[k]}
}


function array-to-string {
    
    for key in ${!_params[@]}
    do
        echo hello...
    done
}
