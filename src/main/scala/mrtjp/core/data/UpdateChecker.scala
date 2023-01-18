/*
 * Copyright (c) 2014.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.core.data

trait UpdateChecker extends Thread
{
    setName(project+" version checker")

    def project:String
    def changelogURL:String

    def currentVersion:String
    def shouldRun:Boolean

    val availableVersions = downloadVersions

    def downloadVersions:Seq[String] =
    {
        Seq.empty
    }

    def isVersionOutdated(v:String) = false

    var updatesChecked = true

    override def run() {}

    start()
}
