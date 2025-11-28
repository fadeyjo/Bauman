# Additional clean files
cmake_minimum_required(VERSION 3.16)

if("${CONFIG}" STREQUAL "" OR "${CONFIG}" STREQUAL "Debug")
  file(REMOVE_RECURSE
  "CMakeFiles/admin_panel_autogen.dir/AutogenUsed.txt"
  "CMakeFiles/admin_panel_autogen.dir/ParseCache.txt"
  "admin_panel_autogen"
  )
endif()
