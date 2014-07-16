/*
 * Copyright (c) 2014 Azavea.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster.op.focal

import geotrellis._
import geotrellis.raster._
import geotrellis.engine._

/** Computes the sum of values of a neighborhood for a given raster 
 *
 * @param    r      Tile on which to run the focal operation.
 * @param    n      Neighborhood to use for this operation (e.g., [[Square]](1))
 *
 * @note            If the neighborhood is a [[Square]] neighborhood, the sum calucation will use
 *                  the [[CellwiseSumCalc]] to perform the calculation, because it is faster.
 *                  If the neighborhood is of any other type, then [[CursorSumCalc]] is used.
 *
 * @note            Sum does not currently support Double raster data.
 *                  If you use a Tile with a Double CellType (TypeFloat, TypeDouble)
 *                  the data values will be rounded to integers.
 */
case class Sum(r: Op[Tile], n: Op[Neighborhood], ns: Op[TileNeighbors] = TileNeighbors.NONE)
  extends FocalOperation0[Tile](r, n, ns)
{
  override def getCalculation(r: Tile, n: Neighborhood) = SumCalculation(r, n)
}