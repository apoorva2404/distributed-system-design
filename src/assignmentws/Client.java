
package assignmentws;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Client {

	private static boolean validateUserId(String ID) {
        String[] locationPrefixes = {"ATW", "OUT", "VER"};
        if(ID.length() == 8 && (ID.charAt(3) == 'C' || ID.charAt(3) == 'A') &&
                (Arrays.asList(locationPrefixes).contains(ID.substring(0, 3))) && (ID.substring(4, 8).matches("[0-9]+") && ID.substring(4, 8).length() == 4)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean validateMovieName(String movieName) {
        String[] movies = {"AVATAR", "AVENGERS", "TITANIC"};
        return Arrays.asList(movies).contains(movieName.toUpperCase());
    }

    private static boolean validateMovieId(String movieId) throws ParseException {
        if(movieId.length() < 10) {
            return false;
        }
        String[] locationPrefixes = {"ATW", "OUT", "VER"};

        Character[] timings = {'M', 'E', 'A'};

        Date current = new Date();

        String movieDateString = movieId.substring(4, 6)+"/"+movieId.substring(6, 8)+"/"+"20" + movieId.substring(8, 10);

        Date movieDate=new SimpleDateFormat("dd/MM/yyyy").parse(movieDateString);

        // Check for 7 days
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);

        if(movieDate.after(current) && movieDate.before(cal.getTime())) {
            if(Arrays.asList(locationPrefixes).contains(movieId.substring(0, 3)) &&
                    (Arrays.asList(timings).contains(movieId.charAt(3)))){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean checkIsNumeric(String capacity) {
        try {
            Integer.parseInt(capacity);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private static boolean isAdmin(String ID) {
        if(ID.charAt(3) == 'A') return true;
        else return false;
    }

    private static boolean isCustomer(String ID) {
        if(ID.charAt(3) == 'C') return true;
        else return false;
    }

    private static void bookMovieTicket(String ID, BookingInterface R, LoggerClass logInfo) throws ParseException, IOException {
        try {
            InputStreamReader is = new InputStreamReader(System.in);

            BufferedReader br = new BufferedReader(is);

            System.out.println("Enter movie name: ");

            String movieName = br.readLine().toUpperCase();

            Boolean isValidMovieName = validateMovieName(movieName);

            if(isValidMovieName) {
                System.out.println("Enter movie ID: ");

                String movieId = br.readLine().toUpperCase();

                Boolean isValidMovieId = validateMovieId(movieId);

                if(isValidMovieId) {
                    System.out.println("Enter Number of tickets to book: ");

                    String noOfTicketsToBook = br.readLine();

                    if(Integer.parseInt(noOfTicketsToBook) > 0 && checkIsNumeric(noOfTicketsToBook)) {
                        String result = R.bookMovieTickets(ID, movieId.toUpperCase(), movieName.toUpperCase(), Integer.parseInt(noOfTicketsToBook), true);

                        logInfo.logger.info(result);

                        System.out.println("Result - " + result);
                    } else {
                        System.out.println("Invalid number! Please enter valid number of tickets ");
                        logInfo.logger.info("Invalid number! Please enter valid number of tickets ");
                    }
                } else {
                    System.out.println("Invalid movie id! Please enter correct movie id. ");
                    logInfo.logger.info("Invalid movie id! Please enter correct movie id. ");
                }
            } else {
                System.out.println("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                logInfo.logger.info("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
            }

        } catch(Exception e) {
            logInfo.logger.info("Exception encountered - " + e.getMessage());
        }

    }

    private static void getBookingSchedule(String ID, BookingInterface R, LoggerClass logInfo) throws RemoteException {
        try {
            String[] message = R.getBookingSchedule(ID, true);

            logInfo.logger.info("" + Arrays.toString(message) + " " + LocalDateTime.now());

            System.out.println("Result - Booked Schedule: ");

            Arrays.asList(message).forEach(name -> {
                System.out.println(name);
            });
        } catch(Exception e) {
            logInfo.logger.info("Exception encountered - " + e.getMessage());
        }
    }

    private static void cancelMovieTicket(String ID, BookingInterface R, LoggerClass logInfo) throws ParseException, IOException {
        try {
            InputStreamReader is = new InputStreamReader(System.in);

            BufferedReader br = new BufferedReader(is);

            System.out.println("Enter movie name: ");

            String movieName = br.readLine().toUpperCase();

            Boolean isValidMovieName = validateMovieName(movieName);

            if(isValidMovieName) {
                System.out.println("Enter movie ID: ");

                String movieId = br.readLine().toUpperCase();

                Boolean isValidMovieId = validateMovieId(movieId);

                if(isValidMovieId) {
                    System.out.println("Enter number of tickets to cancel: ");

                    String noOfTicketsToCancel = br.readLine();

                    String result = R.cancelMovieTickets(ID, movieId.toUpperCase(), movieName.toUpperCase(), Integer.parseInt(noOfTicketsToCancel), true);

                    logInfo.logger.info(result);

                    System.out.println("Result - " + result);
                } else {
                    System.out.println("Invalid movie id! Please enter correct movie id. ");
                    logInfo.logger.info("Invalid movie id! Please enter correct movie id. ");
                }
            } else {
                System.out.println("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                logInfo.logger.info("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
            }
        } catch(Exception e) {
            logInfo.logger.info("Exception encountered - " + e.getMessage());
        }
    }


    private static void exchangeMovieTicket(String ID, BookingInterface R, LoggerClass logInfo) {
        try {
            InputStreamReader is = new InputStreamReader(System.in);

            BufferedReader br = new BufferedReader(is);

            System.out.println("Enter movie ID: ");

            String movieId = br.readLine().toUpperCase();

            Boolean isValidMovieId = validateMovieId(movieId);

            if(isValidMovieId) {
                System.out.println("Enter new movie ID: ");

                String newMovieId = br.readLine().toUpperCase();

                Boolean isValidNewMovieId = validateMovieId(newMovieId);

                if(isValidNewMovieId) {
                    System.out.println("Enter new movie name: ");

                    String movieName = br.readLine().toUpperCase();

                    Boolean isValidMovieName = validateMovieName(movieName);

                    if(isValidMovieName) {
                        System.out.println("Enter number of tickets to exchange: ");

                        String noOfTicketsToExchange = br.readLine();

                        String result = R.exchangeTickets(ID, movieId, newMovieId, movieName, Integer.parseInt(noOfTicketsToExchange));

                        logInfo.logger.info(result);

                        System.out.println("Result - " + result);
                    } else {
                        System.out.println("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                        logInfo.logger.info("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                    }
                } else {
                    System.out.println("Invalid movie id! Please enter correct movie id. ");
                    logInfo.logger.info("Invalid movie id! Please enter correct movie id. ");
                }
            } else {
                System.out.println("Invalid movie id! Please enter correct movie id. ");
                logInfo.logger.info("Invalid movie id! Please enter correct movie id. ");
            }
        } catch (Exception e) {
            System.out.println("Exception in exchange movie tickets " + e);
            e.printStackTrace();
        }
    }
	public static void main(String[] args) throws MalformedURLException{
		URL urlAtwater = new URL("http://localhost:8080/atwater?wsdl");
		URL urlVerdun = new URL("http://localhost:8081/verdun?wsdl");
		URL urlOutrement = new URL("http://localhost:8082/outrement?wsdl");

		QName qNameAtwater = new QName("http://assignmentws/", "BookingImplementationService");
		QName qNameVerdun = new QName("http://assignmentws/", "BookingImplementationService");
		QName qNameOutrement = new QName("http://assignmentws/", "BookingImplementationService");

		Service serviceAtwater = Service.create(urlAtwater, qNameAtwater);
		Service serviceVerdun = Service.create(urlVerdun, qNameVerdun);
		Service serviceOutrement = Service.create(urlOutrement, qNameOutrement);

		BookingInterface atwaterService = serviceAtwater.getPort(BookingInterface.class);
		BookingInterface verdunService = serviceVerdun.getPort(BookingInterface.class);
		BookingInterface outrementService = serviceOutrement.getPort(BookingInterface.class);

		HashMap<String, BookingInterface> services = new HashMap<>();
		services.put("ATW", atwaterService);
		services.put("VER", verdunService);
		services.put("OUT", outrementService);
		// System.out.println(atwaterService.add(10, 20));

		LoggerClass logInfo = null;
        try {
            while(true) {
                String refKey;

                InputStreamReader is = new InputStreamReader(System.in);

                BufferedReader br = new BufferedReader(is);

                System.out.println("Enter Customer ID: ");

                String ID = br.readLine().toUpperCase();

                BookingInterface R;

                Boolean validUser = validateUserId(ID);

                if(validUser) {
                    logInfo = new LoggerClass(ID + ".txt");
                    logInfo.logger.setLevel(Level.ALL);
                    logInfo.logger.info("User Entered ");

                    System.out.println("Valid user " + ID);

                    refKey = ID.substring(0, 3);
                    
                    R = services.get(refKey);

                    System.out.println("Found the reference obj in registry ");

                    if(isAdmin(ID)) {
                        System.out.println("Please select the action you want to perform: ");

                        System.out.println("Press 1 to add movie slots. ");

                        System.out.println("Press 2 to remove movie slots. ");

                        System.out.println("Press 3 to list movie show availability. ");

                        System.out.println("Press 4 to book movie tickets. ");

                        System.out.println("Press 5 to get booking schedule. ");

                        System.out.println("Press 6 to cancel movie tickets. ");

                        System.out.println("Press 7 to exchange movie tickets. ");

                        String userInput = br.readLine();
                        if(Integer.parseInt(userInput) == 1) {
                            System.out.println("Enter movie name: ");

                            String movieName = br.readLine().toUpperCase();

                            Boolean isValidMovieName = validateMovieName(movieName);

                            if(isValidMovieName) {
                                System.out.println("Enter movie ID: ");

                                String movieId = br.readLine().toUpperCase();

                                Boolean isValidMovieId = validateMovieId(movieId);

                                if(isValidMovieId) {
                                    System.out.println("Enter capacity: ");

                                    String capacity = br.readLine();

                                    if(Integer.parseInt(capacity) > 0 && checkIsNumeric(capacity)) {
                                        String result = R.addMovieSlots(movieId.toUpperCase(), movieName, Integer.parseInt(capacity));

                                        logInfo.logger.info(result);

                                        System.out.println("Result - " + result);
                                    } else {
                                        System.out.println("Please enter valid number for booking capacity ");
                                        logInfo.logger.info("Please enter valid number for booking capacity ");
                                    }
                                } else {
                                    System.out.println("Invalid movie id! Please enter correct movie id. ");
                                    logInfo.logger.info("Invalid movie id! Please enter correct movie id. ");
                                }
                            } else {
                                System.out.println("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                                logInfo.logger.info("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                            }
                        } else if(Integer.parseInt(userInput) == 2) {
                            System.out.println("Enter movie name: ");

                            String movieName = br.readLine().toUpperCase();

                            Boolean isValidMovieName = validateMovieName(movieName);

                            if(isValidMovieName) {
                                System.out.println("Enter movie ID: ");

                                String movieId = br.readLine().toUpperCase();

                                Boolean isValidMovieId = validateMovieId(movieId);

                                if(isValidMovieId) {
                                    String result = R.removeMovieSlots(movieId.toUpperCase(), movieName.toUpperCase());

                                    logInfo.logger.info(result);

                                    System.out.println("Result - " + result);
                                } else {
                                    System.out.println("Invalid movie id! Please enter correct movie id. ");
                                    logInfo.logger.info("Invalid movie id! Please enter correct movie id. ");
                                }

                            } else {
                                System.out.println("Incorrect movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                                logInfo.logger.info("Incorrect movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                            }
                        } else if(Integer.parseInt(userInput) == 3) {

                            System.out.println("Enter movie name: ");

                            String movieName = br.readLine().toUpperCase();
                            System.out.println("movieName " + movieName);

                            Boolean isValidMovieName = validateMovieName(movieName);

                            if(isValidMovieName) {
                                String[] result = R.listMovieShowsAvailability(movieName.toUpperCase(), true);

                                System.out.println("Result - Available movie shows: " + Arrays.toString(result));

                                logInfo.logger.info("Available movie shows: " +  Arrays.toString(result) + " " + LocalDateTime.now());
                            } else {
                                System.out.println("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                                logInfo.logger.info("Invalid movie name! Please enter movies from Avatar, Avengers and Titanic. ");
                            }
                        } else if(Integer.parseInt(userInput) == 4) {
                            bookMovieTicket(ID, R, logInfo);
                        } else if(Integer.parseInt(userInput) == 5) {
                            getBookingSchedule(ID, R, logInfo);
                        } else if(Integer.parseInt(userInput) == 6) {
                            cancelMovieTicket(ID, R, logInfo);
                        } else if(Integer.parseInt(userInput) == 7) {
                            exchangeMovieTicket(ID, R, logInfo);
                        } else {
                            System.out.println("Please enter a valid input! ");
                            logInfo.logger.info("Please enter a valid input! ");
                        }
                    } else if(isCustomer(ID)) {
                        System.out.println("Press 1 to book movie tickets. ");

                        System.out.println("Press 2 to get booking schedule. ");

                        System.out.println("Press 3 to cancel movie tickets. ");

                        System.out.println("Press 4 to exchange movie tickets. ");

                        String userInput = br.readLine();
                        if(Integer.parseInt(userInput) == 1) {
                           bookMovieTicket(ID, R, logInfo);
                        } else if(Integer.parseInt(userInput) == 2) {
                            getBookingSchedule(ID, R, logInfo);
                        } else if(Integer.parseInt(userInput) == 3) {
                            cancelMovieTicket(ID, R, logInfo);
                        } else if(Integer.parseInt(userInput) == 4){
                            exchangeMovieTicket(ID, R, logInfo);
                        }else {
                            System.out.println("Please enter a valid number");
                            logInfo.logger.info("Please enter a valid number");
                        }
                    } else {
                        System.out.println("Please enter valid ID");
                        logInfo.logger.info("Please enter valid ID");
                    }
                }
                else {
                    System.out.println("Incorrect ID. Please enter correct ID. ");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logInfo.logger.info("Exception encountered" + e.getMessage());
            System.out.println("Exception in Client " + e);
        }
    }
	}