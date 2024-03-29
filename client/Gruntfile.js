'use strict';

module.exports = function(grunt) {
    //grunt plugins
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-connect');
    grunt.loadNpmTasks('grunt-contrib-requirejs');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-karma');
    grunt.loadNpmTasks('grunt-recess');

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        karma: {
            dev: {
                configFile: 'karma-dev.conf.js',
                singleRun: true
            },
            ci: {
                configFile: 'karma-ci.conf.js',
                singleRun: true
            }
        },
        copy: {
            views: {
                files: [
                    {
                        expand: true,
                        src: ['app/views/**.html'],
                        dest: '../public/javascripts'
                    }
                ]
            }
        },
        watch: {
            src: {
                files: ['app/*.js', 'app/viewmodels/**.js', 'app/util/**.js'],
                tasks: ['requirejs:dev']
            },
            views: {
                files: ['app/views/**.html'],
                tasks: ['copy:views']
            },
            less: {
                files: ['stylesheets/**.less', 'stylesheets/**.css'],
                tasks: ['recess:dist'],
                options: {
                    livereload: true
                }
            },
            dist: {
                files: ['../public/javascripts/**'],
                tasks: ['karma:dev'],
                options: {
                    livereload: true
                }
            },
            test: {
                files: ['test/**'],
                tasks: ['karma:dev']
            }
        },
        recess: {
            dist: {
                options: {
                    compile: true,
                    compress: true,
                    includePath: ['stylesheets/bootstrap/']
                }, files: {
                    "../public/stylesheets/main.css": [
                        'stylesheets/*.less', 'stylesheets/*.css %>'
                    ]
                }
            }
        },
        requirejs: {
            options: {
                optimizeCss: "none",
                generateSourceMaps: true,
                preserveLicenseComments: false,
                baseUrl: 'app',
                dir: '../public/javascripts/app',
                mainConfigFile: 'app/require_wrapper.js',
                modules: [
                    {
                        name: 'main'
                    }
                ]
            },
            dev: {
                options: {
                    optimize: "none"
                }
            },
            dist: {
                options: {
                    optimize: "uglify2"
                }
            }
        }
    });

    grunt.registerTask('dev', [
        'recess:dist',
        'requirejs:dev',
        'copy:views'
    ]);
    grunt.registerTask('run', [
        'dev',
        'watch'
    ]);
};