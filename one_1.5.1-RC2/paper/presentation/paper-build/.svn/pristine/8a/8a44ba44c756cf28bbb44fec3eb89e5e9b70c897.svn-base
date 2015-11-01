#!/usr/bin/ruby

# This programm is used to extract acronyms from LaTeX-files 
#
# Copyright 2008 Wolfgang Kiess

# This program is free software; you can redistribute it and/or modify it under 
# the terms of the GNU General Public License as published by the Free Software 
# Foundation; either version 2 of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT ANY 
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
# PARTICULAR PURPOSE. See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with this 
# program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth 
# Floor, Boston, MA 02110, USA


## Usage:
# Am besten mit 'rake final' ein großes TexFile erzeugen und das # 
# Programm hier darauf loslassen.

puts "Processing file #{ARGV[0]}"

tex_file = ARGV[0] + ".tex"
nlo_file = ARGV[0] + ".nlo"




file = File.open(tex_file, "r")
acronyms = Hash.new()
file.each_line {|line|
   #next if(line =~ /#.*/)
	if line=~ /([A-Z]+)/
#		puts $1
		acronyms[$1] = line
	end
}

puts "Parsing nomenclature file"
nomencl_entries = Hash.new()
file = File.open(nlo_file, "r")
file.each_line {|line|
   #next if(line =~ /#.*/)
	#puts line
	if line=~ /\\nomenclatureentry{a[A-Za-z\s]+@\[\{([A-Za-z\s]+)\}\]/
		#puts $1
		nomencl_entries[$1] = $1
	end
}

puts '################'
acronyms.keys.sort.each {|k|
	if k.length>=2
		if(nomencl_entries.has_key?(k))
			puts "--- #{k}"
		else
			line = acronyms[k]
			if line=~/\\bibitem\[.*/
				# do nothing, we have found a bibitem entry that does not occur as acronym in the text
			else
				puts k + " :: " + line
			end
		end
	end
}
