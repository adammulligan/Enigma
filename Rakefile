require 'rake'
require 'rubygems'
require 'git'
require 'logger'
require 'zlib'
require 'archive/tar/minitar'
require 'find'

include Archive::Tar

@build_dir = Dir.getwd + '/dist'
@tar_file  = 'project.tgz'

desc 'A highly un-optimised and verbose (i.e. made quickly) project builder for distribution'
task :build do
  # Check if git branch has changes
  # if it doesn't continue, otherwise throw an error

  Dir.mkdir(@build_dir) unless File.directory?(@build_dir)

  Dir.chdir("sys/EnigmaApp") do
    enigma_loc = @build_dir+'/enigma';
    Dir.mkdir(enigma_loc)

    FileUtils.cp_r('./dep',enigma_loc)
    FileUtils.cp_r('./res',enigma_loc)
    FileUtils.cp_r('./src/',enigma_loc)

    puts 'Running ant...'
    `ant && ant jar`

    FileUtils.cp('./bin/jar/Enigma.jar',enigma_loc)
    FileUtils.cp('./cert_id_rsa',enigma_loc)
    FileUtils.cp('./cert_id_rsa.pub',enigma_loc)
    FileUtils.cp('./Enigma.properties',enigma_loc)
    FileUtils.cp('./build.xml',enigma_loc)
  end

  # LaTeX compilation
  Dir.chdir("docs/report") do
    # Run latex three times because:
    # http://en.wikibooks.org/wiki/LaTeX/Bibliography_Management#Why_won.27t_LaTeX_generate_any_output.3F
    puts 'Building latex source...'
    `pdflatex Thesis.tex && bibtex Thesis && pdflatex Thesis.tex && pdflatex Thesis.tex`
    FileUtils.cp 'Thesis.pdf', "#{@build_dir}/Thesis.pdf"

    latex_src = "#{@build_dir}/latex_src"
    Dir.mkdir(latex_src) unless File.directory?(latex_src)

    puts 'Copying latex source...'
    ignored_extensions = [".toc",".log",".bbl",".lot",".aux",".blg",".lof",".synctex",".out",".class",".graffle"]
    Find.find("./") do |file|
      next if ignored_extensions.include?(File.extname(file)) || File.fnmatch?('*TSWLatexianTemp*',file)

      if File.directory?(file)
        FileUtils.cp_r file, latex_src+'/'+file
      else
        FileUtils.cp file, latex_src+'/'+file
      end
    end
  end

  puts 'Tarring everything...'
  tgz = Zlib::GzipWriter.new(File.open(@tar_file, 'wb'))
  Minitar.pack(File.basename(@build_dir), tgz)

  puts "Results saved in #{@build_dir}, tarred to #{@tar_file}."
end

task :clean do
  cleanup
end

task :default => "build"

def cleanup
  puts "Removing directory #{@build_dir}. Continue?"

  case STDIN.gets.chomp.downcase
  when 'y' then FileUtils.rm_rf @build_dir  unless !File.exists?(@build_dir)
  end

  jar_dir='sys/EnigmaApp/bin/jar'
  puts "Removing directory #{jar_dir}. Continue?"
  case STDIN.gets.chomp.downcase
  when 'y' then FileUtils.rm_rf jar_dir  unless !File.exists?(jar_dir)
  end

  puts "Removing #{@tar_file}. Continue?"
  case STDIN.gets.chomp.downcase
  when 'y' then FileUtils.rm_rf @tar_file unless !File.exists?(@tar_file)
  end
end
