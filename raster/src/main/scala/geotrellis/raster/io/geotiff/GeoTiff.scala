/*
 * Copyright 2016 Azavea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster.io.geotiff

import geotrellis.raster._
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import geotrellis.raster.io.geotiff.writer.GeoTiffWriter
import geotrellis.vector.{Extent, ProjectedExtent}
import geotrellis.proj4.CRS

/**
 * Holds information on how the data is represented, projected, and any user
 * defined tags.
 */
trait GeoTiffData {
  val cellType: CellType

  def imageData: GeoTiffImageData
  def extent: Extent
  def crs: CRS
  def tags: Tags
  def options: GeoTiffOptions

  def pixelSampleType: Option[PixelSampleType] =
    tags.headTags.get(Tags.AREA_OR_POINT).flatMap { aop =>
      aop match {
        case "AREA" => Some(PixelIsArea)
        case "POINT" => Some(PixelIsPoint)
        case _ => None
      }
    }
}

/**
 * Base trait of GeoTiff. Takes a tile that is of a type equal to or a subtype
 * of CellGrid
 */
trait GeoTiff[T <: CellGrid] extends GeoTiffData {
  def tile: T

  def projectedExtent: ProjectedExtent = ProjectedExtent(extent, crs)
  def projectedRaster: ProjectedRaster[T] = ProjectedRaster(tile, extent, crs)
  def raster: Raster[T] = Raster(tile, extent)
  def rasterExtent: RasterExtent = RasterExtent(extent, tile.cols, tile.rows)

  def mapTile(f: T => T): GeoTiff[T]

  def write(path: String): Unit =
    GeoTiffWriter.write(this, path)

  def toByteArray: Array[Byte] =
    GeoTiffWriter.write(this)
}

/**
 * Companion object to GeoTiff
 */
object GeoTiff {
  def readMultiband(path: String): MultibandGeoTiff =
    MultibandGeoTiff(path)

  def readSingleband(path: String): SinglebandGeoTiff =
    SinglebandGeoTiff(path)

  def apply(path: String): Either[SinglebandGeoTiff, MultibandGeoTiff] = {
    val multiband = MultibandGeoTiff(path)
    if (multiband.tile.bandCount == 1) {
      Left(new SinglebandGeoTiff(tile = multiband.tile.band(0),
        multiband.extent, multiband.crs, multiband.tags, multiband.options))
    } else {
      Right(multiband)
    }
  }

  def apply(tile: Tile, extent: Extent, crs: CRS): SinglebandGeoTiff =
    SinglebandGeoTiff(tile, extent, crs)

  def apply(raster: SinglebandRaster, crs: CRS): SinglebandGeoTiff =
    apply(raster.tile, raster.extent, crs)

  def apply(tile: MultibandTile, extent: Extent, crs: CRS): MultibandGeoTiff =
    MultibandGeoTiff(tile, extent, crs)

  def apply(raster: MultibandRaster, crs: CRS): MultibandGeoTiff =
    apply(raster.tile, raster.extent, crs)

  def apply(projectedRaster: ProjectedRaster[Tile]): SinglebandGeoTiff =
    apply(projectedRaster.raster, projectedRaster.crs)

  def apply(projectedRaster: ProjectedRaster[MultibandTile])(implicit d: DummyImplicit): MultibandGeoTiff =
    apply(projectedRaster.raster, projectedRaster.crs)
}
