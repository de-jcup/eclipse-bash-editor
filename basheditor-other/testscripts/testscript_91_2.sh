# A test case script for #91
for x in y; do
	for package in $pkgs; do
		please $act $package $OPTS "$param1" "$param2" "$param3"
		sts=$?
		if [ $sts -eq 127 ]; then
			echo "Unknown action $act"
			exit $sts
		elif [ $sts -ne $STS_SUCCESS ]; then
			echo "!!Error $sts in $act $package!"
			exit $sts
		fi
	 done
done