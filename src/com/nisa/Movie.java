/*******************************************************
 * Movie
 *
 * Copyright (c) 1999-2010 Aegis Software Inc.,
 * 225 West 34th Street, New York, NY 10122, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Aegis Software Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Aegis.
 *
 * Created on Jan 11, 2015
 *
 * @author NiltonS
 ******************************************************/
package com.nisa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Movie {

    private static final String MOVIE_INPUT_FORMAT = "Enter new movie (format: name:director:year (yyyy) - type 'q' to quit.)";
    
    private final static List<MovieDTO> movies = new ArrayList<MovieDTO>();
    public volatile static boolean run;
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy");
    
    
    private static class MovieDTO {
        String name;
        String director;
        Date year;

        public MovieDTO(String name, String director, Date year) {
            this.name = name;
            this.director = director;
            this.year = year;
        }

        @Override
        public String toString() {
            return String.format("Movie: Name=%s, Director=%s, Release date=%s.",
                    name, director, FORMAT.format(year) );
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((director == null) ? 0 : director.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((year == null) ? 0 : year.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MovieDTO other = (MovieDTO) obj;
            if (director == null) {
                if (other.director != null)
                    return false;
            } else if (!director.equals(other.director))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (year == null) {
                if (other.year != null)
                    return false;
            } else if (!year.equals(other.year))
                return false;
            return true;
        }
        
    }
    
    public Movie() {
        Movie.run = true;
    }
    
    private static class MovieProducer extends Thread {
        BufferedReader in;
        BufferedWriter out;
        String input;

        @Override
        public void run() {
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new BufferedWriter(new OutputStreamWriter(System.out));
            boolean hasError=false;
            while (run) {
                try {
                    write(MOVIE_INPUT_FORMAT);
                    while( (input = in.readLine()) !=null ) {
                        if("q".equals(input.trim())) {
                            write("End of movie input.");
                            run=false;
                            break;
                        }
                        write(String.format("Read line: %s", input)); 
                        String[] split = input.split(":");
                        if(split!=null && split.length==3) {
                            Date year=null;
                            String name=null;
                            String director=null;
                            
                            String yearIn = null;
                            String nameIn = split[0];
                            String dirIn = split[1];
                            
                            if(!nameIn.isEmpty()) {
                                name = nameIn.trim();
                            }
                            if(!dirIn.isEmpty()) {
                                director = dirIn.trim();
                            }
                            try {
                                yearIn = split[2];
                                year = FORMAT.parse(yearIn.trim());
                            } catch (Exception e) {
                                // ignored
                            }
                            if( (name==null||name.isEmpty()) || (director==null||director.isEmpty()) || year==null ) {
                                write(String.format("\nYou've entered an invalid movie's release year: %s.", yearIn));
                            } else {
                                MovieDTO m = new MovieDTO(split[0].trim(), split[1].trim(), year);
                                movies.add(m);
                                write(String.format("Stored movie: %s", m));
                            }
                        } else {
                            write(String.format("\nYou've entered an invalid movie: %s.", input));
                        }
                        write(MOVIE_INPUT_FORMAT);
                    }
                    write("Contents of the movie database:");
                    for (MovieDTO movie : movies) {
                        write(movie.toString());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    hasError = true;
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } 
                if(hasError) {
                    in = new BufferedReader(new InputStreamReader(System.in));
                    out = new BufferedWriter(new OutputStreamWriter(System.out));
                }
            }
        }
        
        private void write(String s) throws IOException {
            out.write(s);
            out.write("\n");
            out.flush();
        }
    }
    
    private static class MovieConsumer extends Thread {
        @Override
        public void run() {
            while(run) {
                
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Movie movie = new Movie();
        MovieProducer producer = new MovieProducer();
        producer.start();
//        producer.join();
    }
}
