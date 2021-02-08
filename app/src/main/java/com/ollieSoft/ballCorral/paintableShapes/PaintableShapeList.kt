package com.ollieSoft.ballCorral.paintableShapes

import com.ollieSoft.ballCorral.utility.Vector

class PaintableShapeList {

    val items = mutableListOf<PaintableShape>()
    val size = items.size

    //These should correspond to the extent of the coordinates of all shapes in the list.
    //Intended use is for scaling, zooming etc
    var shapesUpperLeft = Vector.zero()
    var shapesLowerRight = Vector(1.0,1.0)

}