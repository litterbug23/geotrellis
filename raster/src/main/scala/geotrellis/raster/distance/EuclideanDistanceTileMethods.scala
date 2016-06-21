package geotrellis.raster.distance

import geotrellis.raster.{RasterExtent, Tile}
import geotrellis.util.MethodExtensions
import geotrellis.vector.Point

trait EuclideanDistanceTileMethods extends MethodExtensions[Array[Point]] {
  def euclideanDistanceTile(rasterExtent: RasterExtent): Tile = { EuclideanDistanceTile(self, rasterExtent) }
}
