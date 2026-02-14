package com.gautam.quiz_app.ui.theme


import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.gautam.quiz_app.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val inriaSerifFont = GoogleFont("Inria Serif")
val cantataOne = GoogleFont("Cantata One")
val poppins = GoogleFont("Poppins")
val inter = GoogleFont("Inter")
val alegreya = GoogleFont("Alegreya")
val mitr = GoogleFont("Mitr")
val aboreto = GoogleFont("Aboreto")
val alegreyaSans = GoogleFont("Alegreya Sans")
val oswald = GoogleFont("Oswald")



val inriaSerifFontFamily = FontFamily(Font(googleFont = inriaSerifFont, fontProvider = provider, weight = FontWeight.Normal))
val cantataOneFontFamily = FontFamily(Font(googleFont = cantataOne, fontProvider = provider))
val poppinsFontFamily = FontFamily(Font(googleFont = poppins, fontProvider = provider))
val interFontFamily = FontFamily(Font(googleFont = inter, fontProvider = provider))
val alegreyaFontFamily = FontFamily(Font(googleFont = alegreya, fontProvider = provider))
val mitrFontFamily = FontFamily(Font(googleFont = mitr, fontProvider = provider))
val aboretoFontFamily = FontFamily(Font(googleFont = aboreto, fontProvider = provider))
val alegreyaSansFontFamily = FontFamily(Font(googleFont = alegreyaSans, fontProvider = provider))
val oswaldFontFamily = FontFamily(Font(googleFont = oswald, fontProvider = provider))
