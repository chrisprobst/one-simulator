puts "Starting"  
filename = ARGV[0]

paranthesis_missing = false

file = File.open(filename, "r")
file.each_line do |line|
  if(paranthesis_missing == true)
    # threats the case in which the conference name is spread over multiple 
    # lines: in this case all lines until the first occurence of "}," at
    # the end of the line will be discarded
    line =~ /.*(\},)/   
    if($1.nil?())
      paranthesis_missing = true
    else
      paranthesis_missing = false
    end			
    next
  end
  if(line =~ /(.*booktitle = \{[^:]*).*/)
    puts $1 + "},"
    if(line =~ /(.*booktitle = \{[^:]*).*(\},)/)
      # the finishing paranthesis are their: we are happy
    else
      # the paranthesis are missing: switch to MISSING mode
      paranthesis_missing = true
    end
  else
    puts line
  end
end
