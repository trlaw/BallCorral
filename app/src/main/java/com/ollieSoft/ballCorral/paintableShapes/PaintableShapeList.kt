package com.ollieSoft.ballCorral.paintableShapes

import com.ollieSoft.ballCorral.utility.Vector

class PaintableShapeList : ArrayList<PaintableShape>() {

    //These should correspond to the extent of the coordinates of shapes which should be rendered
    var shapesUpperLeft = Vector.zero()
    var shapesLowerRight = Vector(1.0,1.0)

}