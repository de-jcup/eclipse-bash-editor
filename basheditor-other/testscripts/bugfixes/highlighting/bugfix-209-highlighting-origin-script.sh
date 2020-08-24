#!/bin/bash

# Write out the RPM "Requires" definition(s) for a dependency using a version range.
write_requires_for_dependency_version_range() {
  local dependency_rpm_name=$1
  local dependency_version=$2

  local min_version=$(echo "$dependency_version" | sed -e 's/.*[\[(]\([^,]*\),.*/\1/')  # Extract the 'min' version value from the '[min,max]' or '(min,max)' range.
  local max_version=$(echo "$dependency_version" | sed -e 's/[^\,]*\,\([^])]*\).*/\1/')  # Extract the 'max' version value from the '[min,max]' or '(min,max)' range.

  if [ -z "$min_version" ] && [ -z "$max_version" ]; then  # Are both empty?

    echo "Requires:		$dependency_rpm_name"

  else

    if [ -n "$min_version" ]; then
      if [[ "$dependency_version" = '['* ]]; then
        echo "Requires:		$dependency_rpm_name >= $min_version"
      else
        echo "Requires:		$dependency_rpm_name > $min_version"
      fi
    fi

    if [ -n "$max_version" ]; then
      if [[ "$dependency_version" = *']' ]]; then
        echo "Requires:		$dependency_rpm_name <= $max_version"
      else
        echo "Requires:		$dependency_rpm_name < $max_version"
      fi
    fi

  fi

  return
}
