#!/usr/bin/ruby

# This programm is used to check LaTeX-files for grammatically wrong expressions
# and also respects writing conventions.
#
# Copyright 2006 Wolfgang Kiess

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

## A number of configuration parameters : ##
INITIAL_DIRECTORY = "paper-build/checktex_ruby/"
AVOID_FILES = "avoid_list"   # contains a list of files with expressions to avoid
EXCEPTION_FILE = "exception_list" # contains the expressions that are no errors
EXPRESSION = "expression"   # hash key to the expression to be matched
REPLACEMENT = "replacement" # hash key to the replacement string
COMMENT = "comment"         # hash key to the comment string that says why something has to be replaced

LINE_BUFFER_LENGTH = 2      # the number of lines that should be contatenated for a check (to also
                            # detect problems ranging over a newline)
IGNORE_COMMENTS = true      # Should comments be considered or not?
#IGNORE_COMMENTS = false     # Should comments be considered or not?

@avoid_array = Array.new()  # Array that will contain a number of entries with expressions to avoid
@exception_array = Array.new()  # Array that will contain a number of entries with expressions that
                            # are exceptions (thus would be matched by the avoid-expressions but
                            # are exceptions
@print_exceptions = false
@all_files = false
@multilines = true

# a FIFO line buffer that holds LINE_BUFFER_LENGTH lines
class LineBuffer 
  def initialize()
    @buffer = Array.new(LINE_BUFFER_LENGTH)
    @length_without_last_line = 0   
  end

  # append the given line to the end of the buffer and removes the oldest (FIFO)
  def put(line)
    line_to_insert = line.delete("\n")
    @buffer.shift()
    @buffer.push(line_to_insert)    
    @length_without_last_line = fullLine.length() - line_to_insert.length()
#    puts line 
  end

  # returns a concatenation of the lines in the buffer
  def fullLine()
    return @buffer.join(" ")
  end

  def intersectsLastLine(match)
    if((match.i <= @length_without_last_line) && 
         (match.i + match.l >= @length_without_last_line))
      return true
    else
      return false
    end
  end

end

# holds a match (index and length) and can calculate if two matches intersect
class Match
  attr_reader :i,:l
   def initialize(index,length)
     @i = index
     @l = length
   end

   # prints true if the two matches intersect
   def intersects(m)
     # m matched FULLY before this
     if((m.i < @i) && (m.i + m.l < @i))
       return false
     end
     
     # this matched fully before m
     if((@i < m.i) && (@i + @l < m.i))
       return false
     end
     
     # the two IFs are not true, thus we have an intersection
     return true
   end
end

# Contains the expression to match, replacement and the comment
class Entry
  attr_reader :replacement, :comment
  def initialize(toMatch, replacement, comment)
    @toMatch = toMatch
    @replacement = replacement
    @comment = comment
  end

  # returns the index at which this entry matches (or nil)
  def matches?(line)   
    index = nil
    length = nil
    if(@toMatch.kind_of?(String))
      index = line.index(@toMatch)
      length = @toMatch.length
      return Match.new(index,length) if(index != nil)
    end

    if(@toMatch.kind_of?(Regexp))
      mdata = @toMatch.match(line)
      if(mdata != nil)        
        index = mdata.begin(0)
        length = mdata.to_s().length()  
        return Match.new(index,length)    
      end
    end
    
    # no match (or the toMatch-type is not correct...)
    return nil
  end

end

# inserts a string (or a regexp) in the list with expressions to avoid
# it is also mandatory to say why this is to be replaced (in the comment) and
# to give the alternative (for later use to integrate automatic replacement)
def insertAvoidExpression(avoid, replacement, comment)
  entry = Entry.new(avoid, replacement, comment)
  @avoid_array.push(entry)
end

#puts Dir["*.tex"]

# open and process the file that contains the expressions to avoid
def processAvoidFile(filename)
 # puts "## Processing the file '#{filename}' that contains the expressions to avoid"
  avoid = File.open(filename, "r")
  avoid.each_line {|line|
    next if(line =~ /#.*/)
    begin
      eval "insertAvoidExpression(" + line + ")"
    rescue SyntaxError, ArgumentError
      puts "## ERROR in line #{avoid.lineno}: #{line}"
      puts "## Line will be ignored"
      exit
    end
    # puts @avoid_array.inspect()
  }
end


def insertException(exception,comment)
  entry = Entry.new(exception,"",comment)
  @exception_array.push(entry)
end

# open and process the file that contains the exceptions
def processExceptionFile(filename)
  file = File.open(filename, "r")
  file.each_line {|line|
    next if(line =~ /#.*/)
    begin
      eval "insertException(" + line + ")"
    rescue SyntaxError, ArgumentError
      puts "## ERROR in Exception-file, line #{file.lineno}: #{line}"
      puts "## Line will be ignored"
      exit
    end
    
  }
end

# loads the file in an Array
def loadFileInArray(filename)
  result = Array.new()
  file = File.open(filename, "r")
  file.each_line {|line|
    result.push(line)
  }
  file.close()
  return result
end

def printX(n,sign)
  n.times {|i|
    print sign
  }  
end

def markMatch(match)
  printX(match.i,' ')
  printX(match.l,'^')       
  puts ''  
end

def printExceptionText(prefix, line, line_no, avoid_match, avoid_entry, exception_match, exception_entry)
  if @print_exceptions        
    puts "============================ #{prefix} EXCEPTION for line "  + line_no.to_s()  + "\t ======"                 
    
    puts line                    # print the initial line
    markMatch(avoid_match)       # mark the initial match
    markMatch(exception_match)   # mark the exception
    
    puts "AVOID BECAUSE     :: " + avoid_entry.comment if(avoid_entry.comment != "")
    puts "EXCEPTION BECAUSE :: " + exception_entry.comment  if(exception_entry.comment != "")
    puts ""
  end
end

def printAvoidText(prefix, line, line_no,avoid_match, avoid_entry) 
  puts "------------------------------------ #{prefix} " + line_no.to_s()  + "\t --------------"
  
  puts line                    # print the initial line
  markMatch(avoid_match)            # mark the initial match
  
  if(avoid_entry.replacement != "")
    # print the suggestion
    printX(avoid_match.i,' ')
    puts avoid_entry.replacement        
  end
  
  print "COMMENT: " 
  puts avoid_entry.comment  if(avoid_entry.comment != "")
  puts ""
end

def checkTexFile(filename)
  array = loadFileInArray(filename)

  buffer =  LineBuffer.new()  
  array.each_index {|index|  
    line = array[index]
    # do not process the line if it contains comments (and that is configured)
    next if((line =~ /%.*/) and IGNORE_COMMENTS)
    buffer.put(line)
#    puts buffer.fullLine
    @avoid_array.each {|avoid_entry|
      
      if((avoid_match = avoid_entry.matches?(line)) != nil)    
        # we have a match, now check if it is not an exception
        exception_match = nil
        @exception_array.each {|exception_entry|
          if((exception_match = exception_entry.matches?(line)) != nil)            
            # the exception matches the line, now also check if it intersects 
            # with the previous match
            if(exception_match.intersects(avoid_match))      
              printExceptionText("", line, index+1, avoid_match, avoid_entry, 
                                  exception_match, exception_entry) 
              break # one intersecting match is enough              
            end
          end
        }
        if(exception_match != nil)
          next
        end
        
        printAvoidText("Line", line, index+1, avoid_match, avoid_entry) 
               
      else    
        fullLine =  buffer.fullLine()  
        if(( @multilines &&
               (avoid_match = avoid_entry.matches?(fullLine)) != nil) &&
             (buffer.intersectsLastLine(avoid_match))
           )
          exception_match = nil
          @exception_array.each {|exception_entry|
            if((exception_match = exception_entry.matches?(fullLine)) != nil)            
              # the exception matches the line, now also check if it intersects 
              # with the previous match
              if(exception_match.intersects(avoid_match))      
                printExceptionText("MULTILINE",fullLine, index+1, avoid_match, avoid_entry, 
                                   exception_match, exception_entry) 
                break # one intersecting match is enough              
              end
            end
          }
          if(exception_match != nil)
            next
          end


          printAvoidText("MULTIline", fullLine, index+1, avoid_match, avoid_entry) 
        end      
      end

    }
  }
end

def printHelp 
  puts "This script checks LateX documents for errors.";
  puts "Basic syntax: checktex.rb <options> [<tex-file-to-check>]+"
  puts "Available options are: ";
  puts "  -h | --help (show this help)";
  puts "  -e          (print the lines that are taken out as EXCEPTIONS) DEFAULT : don't print"
  puts "  -a          (process all LaTeX files in the directory) DEFAULT : process only the ones on the command line"
  puts "  -m          (deactivate multilines, i.e. deactivate checking over newlines) DFAULT: activated"
end

def parseCmdLine
  ARGV.each {|value|
    case value
    when "-h" || "--help"
      printHelp()
      exit()
    when "-e"
      @print_exceptions = true
    when "-a"
      @all_files = true
    when "-m"
      @multilines = false
    end
  }
  if(ARGV.length() == 0)
      printHelp()
      exit()
  end
end


parseCmdLine()
# open the avoid files and process them one after the other
file = File.open(INITIAL_DIRECTORY + AVOID_FILES, "r")
file.each_line {|line|
  next if((line =~ /#.*/) and IGNORE_COMMENTS)
  line = line.strip()
  puts "Using '#{line}' in directory #{INITIAL_DIRECTORY}"
  processAvoidFile(INITIAL_DIRECTORY + line)
}


processExceptionFile(INITIAL_DIRECTORY + EXCEPTION_FILE)

if(@all_files)
  if(ARGV.length()>1)
    Dir[ARGV[ARGV.length()-1]+"*.tex"].each {|file|
      puts "################## #{file} ##########################"
      checkTexFile(file)}
  else
    Dir["*.tex"].each {|file|
      puts "################## #{file} ##########################"
      checkTexFile(file)}
  end
else
  ARGV.each{|file|
    puts "################## #{file} ##########################"
    checkTexFile(file)
  }
end

