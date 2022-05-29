package io.github.kabirnayeem99.materialgraphlibrary

import android.graphics.Path
import android.graphics.Region


/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 *
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */
class Bar {
    var color = 0
    var name: String? = null
    var value = 0f
    var path: Path? = null
    var region: Region? = null
    var stackedBar = false
    private val segments: ArrayList<BarStackSegment> = ArrayList<BarStackSegment>()

    fun AddStackValue(segment: BarStackSegment) {
        segments.add(segment)
    }

    val stackedValues: ArrayList<BarStackSegment>
        get() = segments
}