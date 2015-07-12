# Java2Plant
A tool to generate PlantUML source files from java sources.

See https://github.com/athy/Java2Plant

![[img/default-all.png]](https://github.com/forsrc/Java2Plantuml/blob/master/img/default-all.png?raw=true)

```
@startuml img/default-all.png
' ---
package java2plant.model{

class ClassFilter {
- classes:ClassList
- filters:ArrayList<String>
+ ClassFilter(classes:ClassList):
+ getClasses():Collection<ClassDescriber>
+ add(filter:String)
+ getSize():int
+ getElementAt(index:int):Object
+ classExists(className:String):boolean
}
'    ----- end class ClassFilter

 ClassFilter --|> AbstractListModel
}
'    ------------------------ end package java2plant.model

' ---
package java2plant.model{

class ClassList {
- {static} instance:ClassList
- classes:ConcurrentHashMap<String, ClassDescriber>
- ClassList():
+ {static} getInstance():ClassList
+ addClass(c:ClassDescriber)
+ getClass(id:String):ClassDescriber
+ getClass(pack:String,name:String):ClassDescriber
+ classExists(className:String):boolean
+ getClasses():Collection<ClassDescriber>
+ getSize():int
+ getElementAt(index:int):Object
}
'    ----- end class ClassList

 ClassList --|> AbstractListModel
}
'    ------------------------ end package java2plant.model

' ---
package java2plant.describer{

class Visibility {
- visibility:String
+ Visibility(vis:String):
+ toString():String
}
'    ----- end class Visibility

}
'    ------------------------ end package java2plant.describer

' ---
package java2plant.builder{

abstract class AbstractBuilder {
# is:InputStream
# context:ContextDescriber
+ {abstract} buildFromFile(in:File):ContextDescriber
+ {static} splitString(str:String,regex:String):String[]
+ {static} splitString(str:String):String[]
}
'    ----- end class AbstractBuilder

}
'    ------------------------ end package java2plant.builder

' ---
package java2plant.gui{

class MainWindow {
- classList:JList
- filterList:JList
- writeButton:JButton
- actionPanel:JPanel
+ MainWindow():
- initComponants()
+ actionPerformed(e:ActionEvent)
}
'    ----- end class MainWindow

 MainWindow --|> JFrame
}
'    ------------------------ end package java2plant.gui

' ---
package java2plant.describer{

class ContextDescriber {
- namespace:String
- classes:ClassList
- {static} instance:ContextDescriber
- ContextDescriber():
+ {static} getInstance():ContextDescriber
+ setNamespace(namespace:String)
+ addClass(c:ClassDescriber)
+ getClass(pack:String,name:String):ClassDescriber
+ classExists(className:String):boolean
+ getClasses():Collection<ClassDescriber>
+ getNamespace():String
}
'    ----- end class ContextDescriber

}
'    ------------------------ end package java2plant.describer

' ---
package java2plant.describer{

class MethodDescriber {
- visibility:Visibility
- returnType:String
- name:String
- isAbstract:boolean
- isStatic:boolean
- args:ArrayList<ArgumentDescriber>
+ MethodDescriber():
+ getVisibility():Visibility
+ setVisibility(vis:String)
+ getReturnType():String
+ setReturnType(returnType:String)
+ getName():String
+ setName(name:String)
+ isAbstract():boolean
+ setAbstract(isAbstract:boolean)
+ isStatic():boolean
+ setStatic(isStatic:boolean)
+ getArgs():ArrayList<ArgumentDescriber>
+ print()
+ addArg(arg:ArgumentDescriber)
}
'    ----- end class MethodDescriber

}
'    ------------------------ end package java2plant.describer

' ---
package java2plant.builder{

class FromJavaBuilder {
+ FromJavaBuilder():
+ buildFromFile(fInputDir:File):ContextDescriber
+ buildFromStream(inputStream:InputStream):ContextDescriber
+ getNext(is:InputStream):String
+ getNext(src:String):String
+ extractDeclaration(str:String):String
+ buildClassFromString(str:String):ClassDescriber
+ buildMethodFromString(str:String):MethodDescriber
+ buildArgumentFromString(str:String):ArgumentDescriber
+ buildFieldFromString(str:String):FieldDescriber
}
'    ----- end class FromJavaBuilder

 FromJavaBuilder --|> AbstractBuilder
}
'    ------------------------ end package java2plant.builder

' ---
package java2plant.describer{

class ArgumentDescriber {
- type:String
- name:String
+ getType():String
+ setType(type:String)
+ getName():String
+ setName(name:String)
+ toString():String
}
'    ----- end class ArgumentDescriber

}
'    ------------------------ end package java2plant.describer

' ---
package java2plant.model{

class AppData {
- {static} instance:AppData
- inputFile:File
- outputFile:File
- filters:ArrayList<ClassFilter>
- currentFilter:int
- AppData():
+ {static} getInstance():AppData
+ getCurrentFilter():ClassFilter
+ getInputFile():File
+ setInputFile(inputFile:File)
+ getOutputFile():File
+ setOutputFile(outputFile:File)
}
'    ----- end class AppData

}
'    ------------------------ end package java2plant.model

' ---
package java2plant.model{

interface ClassCollection {
+ getClasses():Collection<ClassDescriber>
+ classExists(className:String):boolean
}
'    ----- end class ClassCollection

}
'    ------------------------ end package java2plant.model

' ---
package java2plant.builder{

class FromPlantBuilder {
+ FromPlantBuilder():
+ buildFromStream(inputStream:InputStream):ContextDescriber
+ buildFromFile(inputFile:File):ContextDescriber
- parseRelations(line:String)
}
'    ----- end class FromPlantBuilder

 FromPlantBuilder --|> AbstractBuilder
}
'    ------------------------ end package java2plant.builder

' ---
package java2plant.describer{

class FieldDescriber {
- visibility:Visibility
- name:String
- type:String
- isStatic:boolean
+ setVisibility(vis:String)
+ getVisibility():Visibility
+ setName(name:String)
+ getName():String
+ setType(type:String)
+ getType():String
+ isStatic():boolean
+ setStatic(isStatic:boolean)
+ print()
}
'    ----- end class FieldDescriber

}
'    ------------------------ end package java2plant.describer

' ---
package java2plant.control{

interface ToCtrl {
+ setInputFile(in:File)
+ setOutputFile(out:File)
+ parseJava()
+ writePlant(classes:ClassCollection)
}
'    ----- end class ToCtrl

}
'    ------------------------ end package java2plant.control

' ---
package java2plant.writer{

abstract class AbstractWriter {
+ {static} UML_CALSS:Map<String, String>
+ {static} UML:StringBuffer
+ {abstract} write(fOutputDir:File)
}
'    ----- end class AbstractWriter

}
'    ------------------------ end package java2plant.writer

' ---
package java2plant.control{

class Controller {
- {static} ctrl:Controller
- parser:AbstractBuilder
- writer:AbstractWriter
- Controller():
+ {static} getInstance():Controller
+ setInputFile(in:File)
+ setOutputFile(out:File)
+ parseJava()
+ writePlant(classes:ClassCollection)
}
'    ----- end class Controller

 Controller --|> ToCtrl
}
'    ------------------------ end package java2plant.control

' ---
package java2plant.gui{

interface Gui {
}
'    ----- end class Gui

}
'    ------------------------ end package java2plant.gui

' ---
package java2plant.describer{

class ClassDescriber {
# name:String
# visibility:Visibility
# pack:String
- isInterface:boolean
- isAbstract:boolean
- fields:ArrayList<FieldDescriber>
- methods:ArrayList<MethodDescriber>
- inheritances:ArrayList<String>
+ setName(name:String)
+ getName():String
+ setVisibility(vis:String)
+ getVisibility():Visibility
+ print()
+ addField(fd:FieldDescriber)
+ addMethod(md:MethodDescriber)
+ getPackage():String
+ setPackage(pack:String)
+ isAbstract():boolean
+ setAbstract(isAbstract:boolean)
+ getFields():ArrayList<FieldDescriber>
+ getMethods():ArrayList<MethodDescriber>
+ getInheritances():ArrayList<String>
+ addInheritance(inheritance:String)
+ isInterface():boolean
+ setInterface(isInterface:boolean)
}
'    ----- end class ClassDescriber

}
'    ------------------------ end package java2plant.describer

' ---
package java2plant.writer{

class PlantWriter {
- classes:ClassCollection
- relations:ArrayList<Relation>
+ PlantWriter(classes:ClassCollection):
+ write(fOutputDir:File)
+ relationExists(class1:String,class2:String):boolean
+ addRelation(class1:String,class2:String)
+ writeRelations(fw:FileWriter)
+ writeClass(c:ClassDescriber,fOutputDir:File)
+ writeField(fd:FieldDescriber,bw:BufferedWriter)
+ writeMethod(md:MethodDescriber,bw:BufferedWriter)
+ writeArgument(arg:ArgumentDescriber,bw:BufferedWriter)
+ writeVisibility(vis:Visibility,bw:BufferedWriter)
}
'    ----- end class PlantWriter

 PlantWriter --|> AbstractWriter
}
'    ------------------------ end package java2plant.writer

' ---
package java2plant{

class Java2Plant {
- {static} fInputDir:File
- {static} fOutputDir:File
+ {static} main(args:String[])
}
'    ----- end class Java2Plant

}
'    ------------------------ end package java2plant

' ---
package java2plant.writer{

class Relation {
- class1:String
- class2:String
+ Relation(class1:String,class2:String):
+ getClass1():String
+ setClass1(class1:String)
+ getClass2():String
+ setClass2(class2:String)
}
'    ----- end class Relation

}
'    ------------------------ end package java2plant.writer

ClassFilter --> ClassList

ClassList --> ClassList

AbstractBuilder --> ContextDescriber

ContextDescriber --> ClassList

ContextDescriber --> ContextDescriber

MethodDescriber --> Visibility

MethodDescriber --> ArgumentDescriber

AppData --> AppData

AppData --> ClassFilter

FieldDescriber --> Visibility

Controller --> Controller

Controller --> AbstractBuilder

Controller --> AbstractWriter

ClassDescriber --> Visibility

ClassDescriber --> FieldDescriber

ClassDescriber --> MethodDescriber

PlantWriter --> ClassCollection

PlantWriter --> Relation


@enduml


```

![image](http://www.plantuml.com:80/plantuml/png/nLfVRnCv47_FfrY91pPgzoJwk4ALGaivAWg8SiV3wNJQP9smt6OtyZeKwF7TRposrsDxlKrJfUI148_7dd_-pNZSfqcMPPDDiVYtMB7aI_6rEDdKHID-MxSbgsydaqLTT5ro9l-zg6h1U78xoH8SPLsEus-gJi3O4ibTFkEy-2x7dbyBNZMhqyaHvKy3tcaECrPCd2b2EitFshfc2r6rpNCSF6VTWbTpnkLQHLccQgzSRJ1LxDVL3mQyLIFKxvSrMxD6p4HQDINx9WdJ_Dty2omC4r2AbzzW-qv9zBPOCxDWFc_RcXNDvEVaQGAVJ7uIrfH9O8x9n35EblrtcipcdU35GaZbhgGTdNK6FhXqp0aJuDtTIN9FTD5j9mfHBNucLGF_QnRCSLRlXBPPR3a7E_rUT9-lYerssd7Y6pwpwwVeiNu3iFLhlOVqNRyBkWf_felSNMvAF9vMPMztTnQT9BNNquuRwgg0PJVFtYVYxXbSOq63XfaGLprkm9H6hJve_gowQbxLbVYEKVEr_waj1khQAIcGUqi2HRJg1rX6puxeRfUOxAjQB_g8UlDjLPUeNA4jglSt1dwX9e2gJn9GvNMpsGgGd1Lh65YqZM3VX3mJyfl6rArPxsU2UrpmTWqdde7tS_az7MGo1wJRWERQKh2C2MFELX0vnfpg-w-_xsOaSoFczjJTs-Ppmn-r-6fRzQ5qLLJD9pWExOtD4JAYyqidGpX3DxmIxCLMYBR9BzKtZ1Pu4jyN3QlpI_m2gzZb9VvaW6kLE6lNcxO1GU0CIxnHV8mlMxvcPSho6Owy_0fdTHgpbbqOJYPH0e_cvGK7-DZNW62UUvr6FuhGa1BkEc0t81R9lmE0xyTa5cmm3kLXJ7TCl3NYfBvWqvqX_nUZkLNXBk0AJ718-0hZuOg9pstfXWE1Q0AhMSAPsFBcu_SDYHDYMtbYEWCClU7au3KwdmmLV4NBk1bVRMMoSwpeIPQQ8euaYocL3qDdE9CenW-z-DOfo4G8WOfJwq-F2uTeQC6mrJ-beGEp-_7GH8fHsSXdqwE-2RLKO3SPZ7SOSWFIYLGV7PYHWlNpO6OSl5m_F59wLNlBB7W9P9jLZ_mn5HTkpbnYsZslU3nrzWmgEKEQxHCrJTg3l38UO2Xrypk93ZEXumilem8H2DBxvmnKv8M4a87CQoHKbRyIqy_H0P8XXt9NXCNtfUOnmH3X2cAbjr_5wjXMIBCSaU3od0dfqNUvKxwQQGUhDaA039H7Y1HH44Jl3u0C3ebzWMNdEZZqrI7Fgtizd6qsvuKe8fT3JSMw2WwDF9buF66WtGfl9Bpgapkml2hgEwKQm5jJPlOVhJYC52gVqqNCpLBVkeyKevOhdIh9q45srDL4k_HThut3HOOzVQFGQuGzzEqUp0SLBLGhwjfeYni4tDqBduUtFEpo1rDwACUybnG_oT11ByliaJJyMvqJ04E5zARW7Vl0QamHNLfNZGM44JIbKciuTRJxrNZgveNHUjEjBLt83OhAEyk_qRfn27E7qJc2upxYxr7GcJejvbmteHuIrcMVWhSkCdnipmIlyN1u4DXZNm1oFGLZMMPrB4FnIev1wRPgxU6F6a59iRVoMh4HrL6saVxE9wIZ6VeCyyVLct_EPc-khtFQthIzQJfHHyYBxNBfztrmwtIfJDWNhtTrO9H0UrlaHhFl40lsligbexYRvnVWbzmI3O3ntCSQtPAn1Ar1HfQ-iscWD_cbiKZac52p8CBgG3rOdAgsc3sUhxOL-2CYwIlP97gi1fDJ5M0VbEBuamZQFqb8nmRn_RNHpMab3FGNbX8OQQde8gMi5jTuxw5pl9lGAPQbdnb4WuoouJUcU-M9_LAHSrT7FT9bwTsbD5LfaAvB_rQdDtcllEM9PKQ9jH_Kk82DK3gt3-5x0ESfCTs9WHjqsMqzuKotlW03a5afyPsZLHz4lbgMC11fqz4QzN5oS9-8rCu8KNtoyVgpf6gMNJfTCXAxcJ9IMewi58Myt21Zc8wE-WreeV_Cz5hnryb0-nGSPgGOPJ4xsyftUODRAv5x-d0F-7X0NfmUEKl7yvjSvM9MUXj4pzS8Zxbz3xVOHXWzjB0_XtWYmKaS2mc91enJy-iP1weN-e2L6VO4XN3h1BStvr1Srz42O5rKJOfDO_Cm5RM2tNVVuxdxeJGnQa_aC-y5vSI2xqxHJ_zGuPa7tNhGOQTl7YT3yqzIxtX5p6QaUt0XwVwjmYbzbAPlqWx1Vpn5Sl0k4ZnT-SiFqeDb_1SFEOk-7W_H1xfgfjc5PDEdeOFEdt1ujrv_JwTwF7KgIuyKFBn6w9-eEqOs3id-FNDWIl0au8BDQTZByIVOSFiV)
