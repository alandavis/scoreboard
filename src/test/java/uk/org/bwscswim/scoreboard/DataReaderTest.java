/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2019 Bracknell and Wokingham Swimming Club (BWSC)
 * %%
 * This file is part of BWSC Scoreboard.
 *
 * BWSC Scoreboard is free software: you can redistribute it and/or modify
 * it under the terms of the LGNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BWSC Scoreboard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGNU Lesser General Public License for more details.
 *
 * You should have received a copy of the LGNU Lesser General Public License
 * along with BWSC Scoreboard.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */
package uk.org.bwscswim.scoreboard;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import uk.org.bwscswim.scoreboard.model.Scoreboard;

import java.io.IOException;
import java.io.InputStream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DataReaderTest
{
    public static final String RESET =
                "[16]00000000[01]008010000002048000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000[04]4c[17]\n"; // Opens a new race
    public static final String NEW_EVENT =
                "[16]00000000[01]0040100000[02]Men 100 m Freestyle                  [04]DD[17]\n" +  // Write event title
                "[16]00000000[01]0040100100[02]Ev 2,  Ht 3                          [04]35[17]\n" +  // Write second line of title
                "[16]00000000[01]0040100200[02]1  Harry Mann       WYCS             [04]55[17]\n" +  // Writes lane number, swimmer name and club of swimmer in lane 1
                "[16]00000000[01]0040100300[02]2  Billy Evans      CHAS             [04]79[17]\n" +  // Ditto
                "[16]00000000[01]0040100400[02]3  John Smith       REAS             [04]42[17]\n" +  // Ditto
                "[16]00000000[01]0040100500[02]4  James Jones      AMES             [04]7A[17]\n" +  // Ditto
                "[16]00000000[01]0040100600[02]5  Rob Moore        BRKS             [04]FE[17]\n" +  // Ditto
                "[16]00000000[01]0040100700[02]6  Millie sab                        [04]9B[17]\n";   // Ditto
    public static final String TIMER_ZEROED =
                "[16]00000000[01]0040101100[02]    0.0 [04]9C[17]\n";                                // Displays zero running time
    public static final String TIMER_STARTED =
            "3500[16]00000000[01]0040101100[02]    0.1 [04]9D[17]\n" +                               // Race starts and running time advances 0.1 sec
            "100 [16]00000000[01]0040101100[02]    0.2 [04]9E[17]\n" +
            "100 [16]00000000[01]0040101100[02]    0.3 [04]9F[17]\n" +
            "100 [16]00000000[01]0040101100[02]    0.4 [04]A0[17]\n" +
            "100 [16]00000000[01]0040101100[02]    0.5 [04]A1[17]\n" +
            "100 [16]00000000[01]0040101100[02]    0.6 [04]A2[17]\n" +
            "100 [16]00000000[01]0040101100[02]    0.7 [04]A3[17]\n" +
            "100 [16]00000000[01]0040101100[02]    0.8 [04]A4[17]\n" +
            "100 [16]00000000[01]0040101100[02]    0.9 [04]A5[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.0 [04]9D[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.1 [04]9E[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.2 [04]9F[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.3 [04]A0[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.4 [04]A1[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.5 [04]A2[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.6 [04]A3[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.7 [04]A4[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.8 [04]A5[17]\n" +
            "100 [16]00000000[01]0040101100[02]    1.9 [04]A6[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.0 [04]9E[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.1 [04]9F[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.2 [04]A0[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.3 [04]A1[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.4 [04]A2[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.5 [04]A3[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.6 [04]A4[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.7 [04]A5[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.8 [04]A6[17]\n" +
            "100 [16]00000000[01]0040101100[02]    2.9 [04]A7[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.0 [04]9F[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.1 [04]A0[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.2 [04]A1[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.3 [04]A2[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.4 [04]A3[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.5 [04]A4[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.6 [04]A5[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.7 [04]A6[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.8 [04]A7[17]\n" +
            "100 [16]00000000[01]0040101100[02]    3.9 [04]A8[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.0 [04]A0[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.1 [04]A1[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.2 [04]A2[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.3 [04]A3[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.4 [04]A4[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.5 [04]A5[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.6 [04]A6[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.7 [04]A7[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.8 [04]A8[17]\n" +
            "100 [16]00000000[01]0040101100[02]    4.9 [04]A9[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.0 [04]A1[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.1 [04]A2[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.2 [04]A3[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.3 [04]A4[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.4 [04]A5[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.5 [04]A6[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.6 [04]A7[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.7 [04]A8[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.8 [04]A9[17]\n" +
            "100 [16]00000000[01]0040101100[02]    5.9 [04]AA[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.0 [04]A2[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.1 [04]A3[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.2 [04]A4[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.3 [04]A5[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.4 [04]A6[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.5 [04]A7[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.6 [04]A8[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.7 [04]A9[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.8 [04]AA[17]\n" +
            "100 [16]00000000[01]0040101100[02]    6.9 [04]AB[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.0 [04]A3[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.1 [04]A4[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.2 [04]A5[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.3 [04]A6[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.4 [04]A7[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.5 [04]A8[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.6 [04]A9[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.7 [04]AA[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.8 [04]AB[17]\n" +
            "100 [16]00000000[01]0040101100[02]    7.9 [04]AC[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.0 [04]A4[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.1 [04]A5[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.2 [04]A6[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.3 [04]A7[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.4 [04]A8[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.5 [04]A9[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.6 [04]AA[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.7 [04]AB[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.8 [04]AC[17]\n" +
            "100 [16]00000000[01]0040101100[02]    8.9 [04]AD[17]\n" +
            "100 [16]00000000[01]0040101100[02]    9.0 [04]A5[17]\n";
    public static final String SPLIT_1 =
            "060 [16]00000000[01]0040100700[02]6  Millie sab                9.06 1  [04]F9[17]\n"+  // First split lane 6, 9.06 sec and 1st place
                "[16]00000000[01]0040101100[02]    9.06[04]BB[17]\n";                               // Running time continues
    public static final String SPLITS_23456 =
            "034 [16]00000000[01]0040100600[02]5  Rob Moore        BRKS     9.40 2  [04]5B[17]\n"+  // 2nd split lane 5, 9.40 sec and 2nd place
            "037 [16]00000000[01]0040100500[02]4  James Jones      AMES     9.77 3  [04]E2[17]\n"+  // etc.
            "223 [16]00000000[01]0040101100[02]   10.2 [04]AF[17]\n" +
                "[16]00000000[01]0040100400[02]3  John Smith       REAS    10.19 4  [04]AF[17]\n"+
            "100 [16]00000000[01]0040101100[02]   10.3 [04]B0[17]\n" +
            "100 [16]00000000[01]0040101100[02]   10.4 [04]B1[17]\n" +
            "100 [16]00000000[01]0040101100[02]   10.5 [04]B2[17]\n" +
            "100 [16]00000000[01]0040101100[02]   10.6 [04]B3[17]\n" +
                "[16]00000000[01]0040100300[02]2  Billy Evans      CHAS    10.60 5  [04]E3[17]\n"+
            "100 [16]00000000[01]0040101100[02]   10.7 [04]B4[17]\n" +
            "100 [16]00000000[01]0040101100[02]   10.8 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   10.9 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   11.0 [04]AE[17]\n" +
            "007 [16]00000000[01]0040100200[02]1  Harry Mann       WYCS    11.07 6  [04]C2[17]\n";
    public static final String TIMER_POST_SPLITS =
            "093 [16]00000000[01]0040101100[02]   11.1 [04]AF[17]\n" +
            "100 [16]00000000[01]0040101100[02]   11.2 [04]B0[17]\n" +
            "100 [16]00000000[01]0040101100[02]   11.3 [04]B1[17]\n" +
            "100 [16]00000000[01]0040101100[02]   11.4 [04]B2[17]\n" +
            "100 [16]00000000[01]0040101100[02]   11.5 [04]B3[17]\n" +
            "100 [16]00000000[01]0040101100[02]   11.6 [04]B4[17]\n" +
            "100 [16]00000000[01]0040101100[02]   11.7 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   11.8 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   11.9 [04]B7[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.0 [04]AF[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.1 [04]B0[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.2 [04]B1[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.3 [04]B2[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.4 [04]B3[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.5 [04]B4[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.6 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.7 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.8 [04]B7[17]\n" +
            "100 [16]00000000[01]0040101100[02]   12.9 [04]B8[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.0 [04]B0[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.1 [04]B1[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.2 [04]B2[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.3 [04]B3[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.4 [04]B4[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.5 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.6 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.7 [04]B7[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.8 [04]B8[17]\n" +
            "100 [16]00000000[01]0040101100[02]   13.9 [04]B9[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.0 [04]B1[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.1 [04]B2[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.2 [04]B3[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.3 [04]B4[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.4 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.5 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.6 [04]B7[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.7 [04]B8[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.8 [04]B9[17]\n" +
            "100 [16]00000000[01]0040101100[02]   14.9 [04]BA[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.0 [04]B2[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.1 [04]B3[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.2 [04]B4[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.3 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.4 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.5 [04]B7[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.6 [04]B8[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.7 [04]B9[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.8 [04]BA[17]\n" +
            "100 [16]00000000[01]0040101100[02]   15.9 [04]BB[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.0 [04]B3[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.1 [04]B4[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.2 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.3 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.4 [04]B7[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.5 [04]B8[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.6 [04]B9[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.7 [04]BA[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.8 [04]BB[17]\n" +
            "100 [16]00000000[01]0040101100[02]   16.9 [04]BC[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.0 [04]B4[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.1 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.2 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.3 [04]B7[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.4 [04]B8[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.5 [04]B9[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.6 [04]BA[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.7 [04]BB[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.8 [04]BC[17]\n" +
            "100 [16]00000000[01]0040101100[02]   17.9 [04]BD[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.0 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.1 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.2 [04]B7[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.3 [04]B8[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.4 [04]B9[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.5 [04]BA[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.6 [04]BB[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.7 [04]BC[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.8 [04]BD[17]\n" +
            "100 [16]00000000[01]0040101100[02]   18.9 [04]BE[17]\n" +
            "100 [16]00000000[01]0040101100[02]   19.0 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   19.1 [04]B7[17]\n";
    public static final String CLEAR_65 =
                "[16]00000000[01]0040100700[02]6  Millie sab                        [04]9B[17]\n" + // First split disappears
            "100 [16]00000000[01]0040101100[02]   19.2 [04]B8[17]\n" +
            "100 [16]00000000[01]0040101100[02]   19.3 [04]B9[17]\n" +
            "100 [16]00000000[01]0040101100[02]   19.4 [04]BA[17]\n" +
                "[16]00000000[01]0040100600[02]5  Rob Moore        BRKS             [04]FE[17]\n";  // second split disappears
    public static final String CLEAR_432 =
            "100 [16]00000000[01]0040101100[02]   19.5 [04]BB[17]\n" +
            "100 [16]00000000[01]0040101100[02]   19.6 [04]BC[17]\n" +
            "100 [16]00000000[01]0040101100[02]   19.7 [04]BD[17]\n" +
            "100 [16]00000000[01]0040101100[02]   19.8 [04]BE[17]\n" +
                "[16]00000000[01]0040100500[02]4  James Jones      AMES             [04]7A[17]\n" +
            "100 [16]00000000[01]0040101100[02]   19.9 [04]BF[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.0 [04]AE[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.1 [04]AF[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.2 [04]B0[17]\n" +
                "[16]00000000[01]0040100400[02]3  John Smith       REAS             [04]42[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.3 [04]B1[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.4 [04]B2[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.5 [04]B3[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.6 [04]B4[17]\n" +
                "[16]00000000[01]0040100300[02]2  Billy Evans      CHAS             [04]79[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.7 [04]B5[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.8 [04]B6[17]\n" +
            "100 [16]00000000[01]0040101100[02]   20.9 [04]B7[17]\n" +
            "100 [16]00000000[01]0040101100[02]   21.0 [04]AF[17]\n" +
            "100 [16]00000000[01]0040101100[02]   21.1 [04]B0[17]\n";
    public static final String FINISH =
            //  Missing running times and finishes?
                "[16]00000000[01]0040100300[02]2  Billy Evans      CHAS    21.10 1  [04]E8[17]\n" + // First finish time, lane 2, swimmer name and club, in 21.10 secs, first place
                "[16]00000000[01]0040101100[02]   21.10[04]C0[17]\n" +                              // Running time stops and updates to 1st place time
            "029 [16]00000000[01]0040100200[02]1  Harry Mann       WYCS    21.39 2  [04]B8[17]\n" + // Second finish place
            "033 [16]00000000[01]0040100400[02]3  John Smith       REAS    21.72 3  [04]AF[17]\n" + // etc.
            "034 [16]00000000[01]0040100500[02]4  James Jones      AMES    22.06 4  [04]E6[17]\n" +
            "036 [16]00000000[01]0040100600[02]5  Rob Moore        BRKS    22.42 5  [04]6B[17]\n" +
            "078 [16]00000000[01]0040100700[02]6  Millie sab               23.24 6  [04]0A[17]\n";
    public static final String RESET_BEFORE_RESULT =
            "3500"+RESET;
    public static final String RESULT =
                "[16]00000000[01]003000002[04]3a[17]\n" + // ?? switches to result mode which reorders swimmers from lane order to finish order (unfortunately more data has lane 1 first etc
                "[16]00000000[01]0040100000[02]Men 100 m Freestyle                  [04]DD[17]\n" +
                "[16]00000000[01]0040100100[02]Ev 2,  Ht 3                          [04]35[17]\n" +
                "[16]00000000[01]0040100200[02]P1  Billy Evans      CHAS    21.10 2 [04]E8[17]\n" + // First place, name & club, time and lane no.
                "[16]00000000[01]0040100300[02]P2  Harry Mann       WYCS    21.39 1 [04]18[17]\n" +
                "[16]00000000[01]0040100400[02]P3  John Smith       REAS    21.72 3 [04]DF[17]\n" +
                "[16]00000000[01]0040100500[02]P4  James Jones      AMES    22.06 4 [04]16[17]\n" +
                "[16]00000000[01]0040100600[02]P5  Rob Moore        BRKS    22.42 5 [04]9B[17]\n" +
                "[16]00000000[01]0040100700[02]P6  Millie sab               23.24 6 [04]3A[17]\n";

    private Config config = new Config(null);
    private Scoreboard scoreboard = new Scoreboard(config);
    private RawDisplay display = new RawDisplay(config);
    private DataReader dataReader = new DataReader(config, scoreboard, display);
    private InputStream inputStream;

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
        dataReader.setTrace(false);
    }

    @After
    public void after() throws IOException
    {
        inputStream.close();
    }

    @Test
    public void testDummyInputStream() throws IOException
    {
        long start = System.currentTimeMillis();
        inputStream = new DummyInputStream(
                "[20]The cat[09]\n" +
                        "200 [20]and the dog.", true);

        StringBuilder sb = new StringBuilder();
        for (int i = inputStream.read(); i != -1; i = inputStream.read())
        {
            sb.append((char) i);
        }
        long ms = System.currentTimeMillis() - start;
        System.out.println(ms + "ms: " + sb);
        assertEquals(" The cat\t and the dog.", sb.toString());
        assertTrue("The wait time looks wrong. It was " + ms + "ms", ms >= 190 && ms < 250);
    }

    @Test
    public void testNoData() throws InterruptedException
    {
        inputStream = new DummyInputStream("", false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='', subTitle='', result=false, clock='', swimmers=[]}", scoreboard.toString());
    }

    @Test
    public void testReset() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='', subTitle='', result=false, clock='', swimmers=[]}", scoreboard.toString());
    }

    @Test
    public void testNewEvent() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='', time=''}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='', time=''}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='', time=''}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='', time=''}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='', time=''}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='', time=''}]}", scoreboard.toString());
    }

    @Test
    public void testTimerZeroed() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT +
                TIMER_ZEROED, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='0.0', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='', time=''}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='', time=''}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='', time=''}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='', time=''}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='', time=''}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='', time=''}]}", scoreboard.toString());
    }

    @Test
    public void testTimerStarted() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT +
                TIMER_ZEROED +
                TIMER_STARTED, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='9.0', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='', time=''}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='', time=''}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='', time=''}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='', time=''}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='', time=''}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='', time=''}]}", scoreboard.toString());
    }

    @Test
    public void testSplit1() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                        NEW_EVENT +
                        TIMER_ZEROED +
                        TIMER_STARTED +
                        SPLIT_1, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='9.06', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='', time=''}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='', time=''}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='', time=''}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='', time=''}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='', time=''}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='1st', time='9.06'}]}", scoreboard.toString());
    }

    @Test
    public void testBadSplit() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                NEW_EVENT +
                        TIMER_ZEROED +
                        TIMER_STARTED +
                "060 [16]00000000[01]0040100700[02]too short[04]F9[17]\n",
                false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='9.0', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='', time=''}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='', time=''}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='', time=''}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='', time=''}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='', time=''}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='', time=''}]}", scoreboard.toString());  // place and time not set
    }

    @Test
    public void testSplits2356() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT +
                TIMER_ZEROED +
                TIMER_STARTED +
                SPLIT_1 +
                SPLITS_23456, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='11.0', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='6th', time='11.07'}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='5th', time='10.60'}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='4th', time='10.19'}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='3rd', time='9.77'}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='2nd', time='9.40'}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='1st', time='9.06'}]}", scoreboard.toString());
    }

    @Test
    public void testTimerPostSplits() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT +
                TIMER_ZEROED +
                TIMER_STARTED +
                SPLIT_1 +
                SPLITS_23456 +
                TIMER_POST_SPLITS, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='19.1', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='6th', time='11.07'}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='5th', time='10.60'}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='4th', time='10.19'}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='3rd', time='9.77'}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='2nd', time='9.40'}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='1st', time='9.06'}]}", scoreboard.toString());
    }

    @Test
    public void testClear65() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT +
                TIMER_ZEROED +
                TIMER_STARTED +
                SPLIT_1 +
                SPLITS_23456 +
                TIMER_POST_SPLITS +
                CLEAR_65, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='19.4', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='6th', time='11.07'}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='5th', time='10.60'}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='4th', time='10.19'}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='3rd', time='9.77'}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='', time=''}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='', time=''}]}", scoreboard.toString());
    }

    @Test
    public void testClear432() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT +
                TIMER_ZEROED +
                TIMER_STARTED +
                SPLIT_1 +
                SPLITS_23456 +
                TIMER_POST_SPLITS +
                CLEAR_65 +
                CLEAR_432, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='21.1', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='6th', time='11.07'}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='', time=''}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='', time=''}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='', time=''}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='', time=''}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='', time=''}]}", scoreboard.toString());
    }

    @Test
    public void testFinish() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT +
                TIMER_ZEROED +
                TIMER_STARTED +
                SPLIT_1 +
                SPLITS_23456 +
                TIMER_POST_SPLITS +
                CLEAR_65 +
                CLEAR_432 +
                FINISH, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=false, clock='21.10', swimmers=[" +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='2nd', time='21.39'}, " +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='1st', time='21.10'}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='3rd', time='21.72'}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='4th', time='22.06'}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='5th', time='22.42'}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='6th', time='23.24'}]}", scoreboard.toString());
    }

    @Test
    public void testResetBeforeResult() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT +
                TIMER_ZEROED +
                TIMER_STARTED +
                SPLIT_1 +
                SPLITS_23456 +
                TIMER_POST_SPLITS +
                CLEAR_65 +
                CLEAR_432 +
                FINISH +
                RESET_BEFORE_RESULT, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='', subTitle='', result=false, clock='', swimmers=[]}", scoreboard.toString());
    }

    @Test
    public void testResult() throws InterruptedException
    {
        inputStream = new DummyInputStream(
                RESET +
                NEW_EVENT +
                TIMER_ZEROED +
                TIMER_STARTED +
                SPLIT_1 +
                SPLITS_23456 +
                TIMER_POST_SPLITS +
                CLEAR_65 +
                CLEAR_432 +
                FINISH +
                RESET_BEFORE_RESULT +
                RESULT, false);

        dataReader.setInputStream(inputStream);
        dataReader.readInputStream();
        assertEquals("Scoreboard{title='Men 100 m Freestyle', subTitle='Ev 2,  Ht 3', result=true, clock='', swimmers=[" +
                "Swimmer{name='Billy Evans', club='CHAS', lane='2', place='1st', time='21.10'}, " +
                "Swimmer{name='Harry Mann', club='WYCS', lane='1', place='2nd', time='21.39'}, " +
                "Swimmer{name='John Smith', club='REAS', lane='3', place='3rd', time='21.72'}, " +
                "Swimmer{name='James Jones', club='AMES', lane='4', place='4th', time='22.06'}, " +
                "Swimmer{name='Rob Moore', club='BRKS', lane='5', place='5th', time='22.42'}, " +
                "Swimmer{name='Millie sab', club='', lane='6', place='6th', time='23.24'}]}", scoreboard.toString());
    }
}