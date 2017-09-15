alpha() {
	# Just a test case for bracket support:

 	a=${b}
    c={d[e]}
    # next line is NOT problematic $ is followed by space
    f=$ {g[h]}
    # next line is problematic because of $ ?
    i=${j[k] }
}

