if [ $opt_aaeD - eq 1 -a $prdstk_level - gt 0 ] || [ $opt_aaeR -eq 1 -a $prdstk_level -eq 0 ]; then
  ij=I
  DETECTED_YES="$DETECTED_YES $4"
elif [ $opt_aaeD - eq 2 -a $prdstk_level - gt 0 ] || [ $opt_aaeR -eq 2 -a $prdstk_level -eq 0 ]; then
  ij=I
  DETECTED_YES="$DETECTED_YES $4"
elif [ $opt_aaeD - eq 3 -a $prdstk_level - gt 0 ] || [ $opt_aaeR -eq 3 -a $prdstk_level -eq 0 ]; then
  act="update"
  DETECTED_UPD="$DETECTED_UPD $4"
else
  return $STS_FAILED
fi 