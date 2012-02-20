require 'rake'
require 'rubygems'
require 'git'
require 'logger'

@build_dir = './dist'

desc 'Build project for distribution'
task :build do
  # Check if git branch has changes
  # if it doesn't continue, otherwise throw an error
  # latex Thesis.tex
  # bibtex Thesis.aux
  # mkdir build
  # cp:
  #   README
  #   docs/report/Thesis.pdf
  #     mkdir build/latex_src
  #     build latex from source
  #     cp docs/report/* ./build/latex_src (minus logs, etc)
  # javac sys/EnigmaApp/src
  # cp sys/EnigmaApp
  #
  # Final structure should be:
  #
  # build/
  #   Enigma/
  #     src/
  #     bin/
  #     dep/
  #     res/
  #   report/
  #     Thesis.pdf
  #     latex_src/
  #   README.md
  Dir.mkdir(@build_dir) unless File.directory?(@build_dir)

  # LaTeX compilation
  Dir.chdir("docs/report") do
    # Run latex three times because:
    # http://en.wikibooks.org/wiki/LaTeX/Bibliography_Management#Why_won.27t_LaTeX_generate_any_output.3F
    `pdflatex Thesis.tex && bibtex Thesis && pdflatex Thesis.tex && pdflatex Thesis.tex`
  end
end

task :clean do
  cleanup
end

task :default => "build"

def cleanup
  puts "Removing directory #{@build_dir}. Continue?"

  case STDIN.gets.chomp.downcase
  when 'y' then FileUtils.rm_rf @build_dir
  end
end
